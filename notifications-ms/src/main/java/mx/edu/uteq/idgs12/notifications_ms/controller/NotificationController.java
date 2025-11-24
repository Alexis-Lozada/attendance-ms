package mx.edu.uteq.idgs12.notifications_ms.controller;

import mx.edu.uteq.idgs12.notifications_ms.dto.NotificationDTO;
import mx.edu.uteq.idgs12.notifications_ms.entity.Notification;
import mx.edu.uteq.idgs12.notifications_ms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint general para enviar correos (texto plano o HTML).
     */
    @PostMapping("/email")
    public ResponseEntity<Notification> sendEmail(@RequestBody NotificationDTO request) {
        Notification notification = notificationService.sendEmail(request);
        return ResponseEntity.ok(notification);
    }

    /**
     * Endpoint específico para enviar correos de asistencia con plantilla.
     */
    @PostMapping("/email/attendance")
    public ResponseEntity<Notification> sendAttendanceEmail(@RequestBody NotificationDTO request) {
        request.setTemplateName("attendance_email_template.html");
        Notification notification = notificationService.sendEmail(request);
        return ResponseEntity.ok(notification);
    }
}
