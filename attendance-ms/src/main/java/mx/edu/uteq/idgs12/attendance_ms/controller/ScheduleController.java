package mx.edu.uteq.idgs12.attendance_ms.controller;

import mx.edu.uteq.idgs12.attendance_ms.dto.schedule.GroupCourseWithSchedulesDTO;
import mx.edu.uteq.idgs12.attendance_ms.dto.schedule.ScheduleGroupRequest;
import mx.edu.uteq.idgs12.attendance_ms.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /** Obtener todo el horario de un grupo (todas las materias con sus horarios) */
    @GetMapping("/group/{idGroup}")
    public ResponseEntity<List<GroupCourseWithSchedulesDTO>> getSchedulesByGroup(@PathVariable Integer idGroup) {
        List<GroupCourseWithSchedulesDTO> schedules = scheduleService.getSchedulesByGroup(idGroup);
        if (schedules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(schedules);
    }

    /** Crear o actualizar todo el horario de un grupo (varias materias con horarios) */
    @PostMapping("/group")
    public ResponseEntity<?> createOrUpdateGroupSchedules(@RequestBody ScheduleGroupRequest request) {
        try {
            List<GroupCourseWithSchedulesDTO> saved = scheduleService.createOrUpdateGroupSchedules(request);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Obtener el horario más cercano o en curso según la fecha y hora del usuario */
    @GetMapping("/closest")
    public ResponseEntity<?> getClosestSchedule(
            @RequestParam Integer idGroupCourse,
            @RequestParam String dateTime) {
        try {
            return scheduleService.getClosestSchedule(idGroupCourse, dateTime)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.noContent().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
