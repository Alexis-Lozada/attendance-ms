package mx.edu.uteq.idgs12.attendance_ms.client;

import mx.edu.uteq.idgs12.attendance_ms.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-ms")
public interface NotificationsFeignClient {

    /** Envía un correo con plantilla HTML a través del microservicio de notificaciones */
    @PostMapping("/api/notifications/email/attendance")
    void sendAttendanceEmail(@RequestBody NotificationDTO request);
}
