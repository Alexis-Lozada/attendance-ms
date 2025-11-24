package mx.edu.uteq.idgs12.attendance_ms.controller;

import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import mx.edu.uteq.idgs12.attendance_ms.service.AttendanceSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class AttendanceSessionController {

    @Autowired
    private AttendanceSessionService sessionService;

    /**
     * Iniciar una nueva sesión de pase de lista (envía correos).
     */
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody AttendanceSession session) {
        try {
            AttendanceSession created = sessionService.startSession(session);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtener todas las sesiones.
     */
    @GetMapping
    public ResponseEntity<List<AttendanceSession>> getAll() {
        List<AttendanceSession> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Obtener una sesión por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return sessionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
