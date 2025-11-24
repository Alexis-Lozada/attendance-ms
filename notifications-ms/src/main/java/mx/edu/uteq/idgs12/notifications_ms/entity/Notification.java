package mx.edu.uteq.idgs12.notifications_ms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotification;

    private String recipientEmail;
    private String subject;
    private String message;
    private String status; // PENDING, SENT, FAILED
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime sentAt;
}
