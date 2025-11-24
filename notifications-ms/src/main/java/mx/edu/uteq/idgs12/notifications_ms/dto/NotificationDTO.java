package mx.edu.uteq.idgs12.notifications_ms.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NotificationDTO {
    private String recipientEmail;
    private String subject;
    private String message;

    // Campos adicionales para plantillas HTML
    private String templateName; // ej. "attendance_email_template.html"
    private Map<String, Object> templateVariables; // ej. { "studentName": "Alexis", "courseName": "POO", "attendanceLink": "..." }
}
