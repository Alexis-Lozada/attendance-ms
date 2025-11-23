package mx.edu.uteq.idgs12.attendance_ms.service;

import mx.edu.uteq.idgs12.attendance_ms.dto.schedule.*;
import mx.edu.uteq.idgs12.attendance_ms.entity.GroupCourse;
import mx.edu.uteq.idgs12.attendance_ms.entity.Schedule;
import mx.edu.uteq.idgs12.attendance_ms.repository.GroupCourseRepository;
import mx.edu.uteq.idgs12.attendance_ms.repository.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private GroupCourseRepository groupCourseRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    /** Obtiene todo el horario de un grupo con sus materias y profesores */
    public List<GroupCourseWithSchedulesDTO> getSchedulesByGroup(Integer idGroup) {
        List<GroupCourse> groupCourses = groupCourseRepository.findByIdGroup(idGroup);

        return groupCourses.stream().map(gc -> {
            List<Schedule> schedules = scheduleRepository.findByIdGroupCourse(gc.getIdGroupCourse());
            GroupCourseWithSchedulesDTO dto = new GroupCourseWithSchedulesDTO();
            BeanUtils.copyProperties(gc, dto);
            dto.setSchedules(
                    schedules.stream().map(s -> {
                        ScheduleDTO sDto = new ScheduleDTO();
                        BeanUtils.copyProperties(s, sDto);
                        return sDto;
                    }).collect(Collectors.toList())
            );
            return dto;
        }).collect(Collectors.toList());
    }

    /** Crea o actualiza una relación (grupo–curso–profesor)*/
    @Transactional
    public GroupCourseWithSchedulesDTO createOrUpdateWithSchedules(ScheduleCreateRequest request) {
        // Buscar si ya existe la relación grupo-curso-profesor
        List<GroupCourse> existingRelations = groupCourseRepository.findByIdGroup(request.getIdGroup()).stream()
                .filter(gc -> gc.getIdCourse().equals(request.getIdCourse()) &&
                              gc.getIdProfessor().equals(request.getIdProfessor()))
                .collect(Collectors.toList());

        GroupCourse relation;
        if (existingRelations.isEmpty()) {
            relation = new GroupCourse();
            relation.setIdGroup(request.getIdGroup());
            relation.setIdCourse(request.getIdCourse());
            relation.setIdProfessor(request.getIdProfessor());
            relation = groupCourseRepository.save(relation);
        } else {
            relation = existingRelations.get(0);
        }

        final Integer relationId = relation.getIdGroupCourse();

        // Sincronizar horarios
        List<Schedule> existingSchedules = scheduleRepository.findByIdGroupCourse(relationId);

        // Mapa para buscar rápidamente los existentes
        Map<Integer, Schedule> existingMap = existingSchedules.stream()
                .collect(Collectors.toMap(Schedule::getIdSchedule, s -> s));

        // Crear o actualizar según ID
        List<Schedule> updatedSchedules = request.getSchedules().stream().map(s -> {
            Schedule entity;
            if (s.getIdSchedule() != null && existingMap.containsKey(s.getIdSchedule())) {
                // Actualizar existente
                entity = existingMap.get(s.getIdSchedule());
                BeanUtils.copyProperties(s, entity, "idSchedule", "idGroupCourse");
            } else {
                // Crear nuevo
                entity = new Schedule();
                BeanUtils.copyProperties(s, entity);
                entity.setIdGroupCourse(relationId);
            }
            return entity;
        }).collect(Collectors.toList());

        // Guardar los cambios (crea o actualiza)
        scheduleRepository.saveAll(updatedSchedules);

        // Eliminar los que ya no estén en el request
        Set<Integer> requestIds = request.getSchedules().stream()
                .map(ScheduleDTO::getIdSchedule)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Schedule> toDelete = existingSchedules.stream()
                .filter(s -> !requestIds.contains(s.getIdSchedule()))
                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            scheduleRepository.deleteAll(toDelete);
        }

        // Construir la respuesta
        GroupCourseWithSchedulesDTO response = new GroupCourseWithSchedulesDTO();
        BeanUtils.copyProperties(relation, response);
        response.setSchedules(
                updatedSchedules.stream().map(s -> {
                    ScheduleDTO dto = new ScheduleDTO();
                    BeanUtils.copyProperties(s, dto);
                    return dto;
                }).collect(Collectors.toList())
        );

        return response;
    }

    /** Crea o actualiza todos los horarios de un grupo completo (varias materias) */
    @Transactional
    public List<GroupCourseWithSchedulesDTO> createOrUpdateGroupSchedules(ScheduleGroupRequest request) {
        return request.getGroupCourses().stream().map(courseReq -> {
            courseReq.setIdGroup(request.getIdGroup());
            return createOrUpdateWithSchedules(courseReq);
        }).collect(Collectors.toList());
    }

    /** Obtiene el horario más cercano o en curso según la fecha y hora del usuario */
    public Optional<ScheduleDTO> getClosestSchedule(Integer idGroupCourse, String dateTime) {
        List<Schedule> schedules = scheduleRepository.findByIdGroupCourse(idGroupCourse);
        if (schedules.isEmpty()) return Optional.empty();

        java.time.LocalDateTime now = java.time.LocalDateTime.parse(dateTime);
        java.time.DayOfWeek today = now.getDayOfWeek();
        java.time.LocalTime currentTime = now.toLocalTime();

        Schedule closest = null;
        long minMinutes = Long.MAX_VALUE;

        for (Schedule s : schedules) {
            try {
                java.time.DayOfWeek scheduleDay = java.time.DayOfWeek.valueOf(s.getDayOfWeek().toUpperCase());
                java.time.LocalTime start = java.time.LocalTime.parse(s.getStartTime());

                // Diferencia de días (0 = hoy)
                int dayDiff = (scheduleDay.getValue() - today.getValue() + 7) % 7;

                // Si el horario ya pasó hoy, lo contamos para la próxima semana
                if (dayDiff == 0 && start.isBefore(currentTime)) {
                    dayDiff = 7;
                }

                java.time.LocalDateTime scheduleDateTime = now.plusDays(dayDiff)
                        .withHour(start.getHour())
                        .withMinute(start.getMinute());

                long diffMinutes = java.time.Duration.between(now, scheduleDateTime).toMinutes();

                if (diffMinutes >= 0 && diffMinutes < minMinutes) {
                    minMinutes = diffMinutes;
                    closest = s;
                }
            } catch (Exception e) {
                // Ignorar errores de formato
            }
        }

        if (closest == null) return Optional.empty();

        ScheduleDTO dto = new ScheduleDTO();
        BeanUtils.copyProperties(closest, dto);
        return Optional.of(dto);
    }
}
