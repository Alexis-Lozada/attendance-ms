package mx.edu.uteq.idgs12.attendance_ms.service;

import mx.edu.uteq.idgs12.attendance_ms.client.UsersFeignClient;
import mx.edu.uteq.idgs12.attendance_ms.dto.AttendanceDTO;
import mx.edu.uteq.idgs12.attendance_ms.dto.AttendanceMarkDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.Attendance;
import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import mx.edu.uteq.idgs12.attendance_ms.entity.Schedule;
import mx.edu.uteq.idgs12.attendance_ms.repository.AttendanceRepository;
import mx.edu.uteq.idgs12.attendance_ms.repository.AttendanceSessionRepository;
import mx.edu.uteq.idgs12.attendance_ms.repository.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AttendanceSessionRepository sessionRepository;

    @Autowired
    private UsersFeignClient usersFeignClient;

    /** 🔹 Obtener asistencia por ID */
    public Optional<AttendanceDTO> getById(Integer id) {
        return attendanceRepository.findById(id).map(this::toDTO);
    }

    /** 🔹 Obtener todas las asistencias por horario */
    public List<AttendanceDTO> getBySchedule(Integer idSchedule) {
        return attendanceRepository.findByIdSchedule(idSchedule)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** 🔹 Obtener asistencias por estudiante */
    public List<AttendanceDTO> getByStudent(Integer idStudent) {
        return attendanceRepository.findByIdStudent(idStudent)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** 🔹 Obtener todas las asistencias por GroupCourse (todas las schedules de ese curso) */
    public List<AttendanceDTO> getByGroupCourse(Integer idGroupCourse) {
        List<Schedule> schedules = scheduleRepository.findByIdGroupCourse(idGroupCourse);
        if (schedules.isEmpty()) return List.of();

        List<Integer> scheduleIds = schedules.stream()
                .map(Schedule::getIdSchedule)
                .toList();

        return attendanceRepository.findByIdScheduleIn(scheduleIds)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** 🔹 Guardar asistencia manual o programática */
    @Transactional
    public AttendanceDTO save(AttendanceDTO dto) {
        Attendance entity = new Attendance();
        BeanUtils.copyProperties(dto, entity);
        Attendance saved = attendanceRepository.save(entity);
        return toDTO(saved);
    }

    /** 🔹 Eliminar asistencia */
    @Transactional
    public void delete(Integer id) {
        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException("Attendance not found with ID: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    /** 🔹 Marcar asistencia automáticamente (por sesión activa) */
    @Transactional
    public AttendanceDTO markAttendance(AttendanceMarkDTO dto) {
        // Buscar sesión activa (OPEN) por grupo-curso
        AttendanceSession session = sessionRepository
                .findTopByIdGroupCourseAndStatus(dto.getIdGroupCourse(), "OPEN")
                .orElseThrow(() -> new RuntimeException("No hay una sesión activa para este grupo."));

        // Validar tiempo de expiración
        if (Instant.now().isAfter(session.getExpiresAt())) {
            throw new RuntimeException("La sesión de asistencia ya expiró.");
        }

        // Validar ubicación si aplica
        if (session.getGeoLatitude() != null && session.getGeoLongitude() != null) {
            double distancia = calcularDistancia(
                    session.getGeoLatitude(), session.getGeoLongitude(),
                    dto.getLatitude(), dto.getLongitude()
            );

            if (distancia > 150) {
                throw new RuntimeException("Estás fuera del rango permitido para marcar asistencia.");
            }
        }

        // Validar duplicado del mismo día
        boolean alreadyExists = !attendanceRepository
                .findTodayAttendance(session.getIdSchedule(), dto.getIdStudent())
                .isEmpty();

        if (alreadyExists) {
            throw new RuntimeException("Ya has marcado asistencia para este curso el día de hoy.");
        }

        // Crear y guardar asistencia
        Attendance attendance = new Attendance();
        attendance.setIdSchedule(session.getIdSchedule());
        attendance.setIdStudent(dto.getIdStudent());
        attendance.setAttendanceDate(Instant.now());
        attendance.setStatus("PRESENT");

        Attendance saved = attendanceRepository.save(attendance);
        return toDTO(saved);
    }

    /** 🔹 Calcula distancia (Haversine en metros) */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radio terrestre en metros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /** 🔹 Convierte entidad a DTO enriquecido con datos del usuario */
    private AttendanceDTO toDTO(Attendance entity) {
        AttendanceDTO dto = new AttendanceDTO();
        BeanUtils.copyProperties(entity, dto);

        try {
            Map<String, Object> user = usersFeignClient.getUserById(entity.getIdStudent());
            if (user != null) {
                String firstName = (String) user.get("firstName");
                String lastName = (String) user.get("lastName");
                dto.setStudentName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                dto.setEnrollmentNumber((String) user.get("enrollmentNumber"));
                dto.setProfileImage((String) user.get("profileImage"));
            }
        } catch (Exception e) {
            dto.setStudentName("Desconocido");
            dto.setEnrollmentNumber(null);
            dto.setProfileImage(null);
        }

        return dto;
    }
}
