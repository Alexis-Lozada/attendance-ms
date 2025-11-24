package mx.edu.uteq.idgs12.attendance_ms.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NotificationDTO {
    private String recipientEmail;
    private String subject;
    private String message; // opcional (para texto plano)
    private String templateName; // ej: "attendance_email_template.html"
    private Map<String, Object> templateVariables; // variables para Thymeleaf
}
