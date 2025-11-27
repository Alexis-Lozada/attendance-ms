package mx.edu.uteq.idgs12.attendance_ms.controller;

import mx.edu.uteq.idgs12.attendance_ms.dto.AttendanceSessionDTO;
import mx.edu.uteq.idgs12.attendance_ms.entity.AttendanceSession;
import mx.edu.uteq.idgs12.attendance_ms.service.AttendanceSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class AttendanceSessionController {

    @Autowired
    private AttendanceSessionService sessionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /** Validar si el profesor puede iniciar sesión*/
    @GetMapping("/can-start")
    public ResponseEntity<Map<String, Boolean>> canStart(
            @RequestParam Integer groupCourse,
            @RequestParam Integer schedule) {

        boolean canStart = sessionService.canStartSession(groupCourse, schedule);

        return ResponseEntity.ok(Collections.singletonMap("canStart", canStart));
    }

    /** Iniciar una nueva sesión de pase de lista (envía correos) */
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody AttendanceSessionDTO dto) {
        try {
            AttendanceSession created = sessionService.startSession(dto);

            // Notificar a WebSocket que la sesión está abierta
            messagingTemplate.convertAndSend(
                    "/topic/sessions/group-course/" + created.getIdGroupCourse(),
                    created
            );

            return ResponseEntity.ok(created);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
