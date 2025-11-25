package mx.edu.uteq.idgs12.attendance_ms.controller;

import mx.edu.uteq.idgs12.attendance_ms.dto.AttendanceSessionDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import mx.edu.uteq.idgs12.attendance_ms.service.AttendanceSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class AttendanceSessionController {

    @Autowired
    private AttendanceSessionService sessionService;

    /**
     * Iniciar una nueva sesión de pase de lista (envía correos).
     */
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody AttendanceSessionDTO dto) {
        try {
            AttendanceSession created = sessionService.startSession(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
