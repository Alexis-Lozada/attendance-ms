package mx.edu.uteq.idgs12.attendance_ms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_sessions")
@Data
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSession;

    @Column(nullable = false)
    private Integer idGroupCourse;

    @Column(nullable = false)
    private Integer idProfessor;

    private Double geoLatitude;
    private Double geoLongitude;

    private String status; // OPEN, CLOSED
    private LocalDateTime startTime;
    private LocalDateTime expiresAt;
}
