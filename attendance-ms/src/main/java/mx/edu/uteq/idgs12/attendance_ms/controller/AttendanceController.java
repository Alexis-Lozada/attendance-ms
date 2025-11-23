package mx.edu.uteq.idgs12.attendance_ms.controller;

import mx.edu.uteq.idgs12.attendance_ms.dto.AttendanceDTO;
import mx.edu.uteq.idgs12.attendance_ms.repository.ScheduleRepository;
import mx.edu.uteq.idgs12.attendance_ms.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /** 🔹 Obtener asistencia por ID */
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getById(@PathVariable Integer id) {
        Optional<AttendanceDTO> dto = attendanceService.getById(id);
        return dto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /** 🔹 Obtener asistencias por horario */
    @GetMapping("/schedule/{idSchedule}")
    public List<AttendanceDTO> getBySchedule(@PathVariable Integer idSchedule) {
        return attendanceService.getBySchedule(idSchedule);
    }

    /** 🔹 Obtener asistencias por estudiante */
    @GetMapping("/student/{idStudent}")
    public List<AttendanceDTO> getByStudent(@PathVariable Integer idStudent) {
        return attendanceService.getByStudent(idStudent);
    }

    /** 🔹 Obtener asistencias por GroupCourse */
    @GetMapping("/group-course/{idGroupCourse}")
    public List<AttendanceDTO> getByGroupCourse(@PathVariable Integer idGroupCourse) {
        return attendanceService.getByGroupCourse(idGroupCourse);
    }

    /** 🔹 Crear nueva asistencia */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AttendanceDTO dto) {
        try {
            AttendanceDTO saved = attendanceService.save(dto);

            // 📡 Obtener idGroupCourse desde el Schedule asociado
            if (saved.getIdSchedule() != null) {
                scheduleRepository.findById(saved.getIdSchedule()).ifPresent(schedule ->
                    messagingTemplate.convertAndSend(
                        "/topic/attendances/group-course/" + schedule.getIdGroupCourse(),
                        saved
                    )
                );
            }

            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** 🔹 Actualizar asistencia existente */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AttendanceDTO dto) {
        try {
            dto.setIdAttendance(id);
            AttendanceDTO updated = attendanceService.save(dto);

            // 📡 Notificar por curso asociado
            if (updated.getIdSchedule() != null) {
                scheduleRepository.findById(updated.getIdSchedule()).ifPresent(schedule ->
                    messagingTemplate.convertAndSend(
                        "/topic/attendances/group-course/" + schedule.getIdGroupCourse(),
                        updated
                    )
                );
            }

            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** 🔹 Eliminar asistencia */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            attendanceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
