package mx.edu.uteq.idgs12.auth_server.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(nullable = false)
    private Integer idUniversity;

    @Column(nullable = true)
    private Integer idDivision;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String enrollmentNumber;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    private String role; // ADMIN, STUDENT, TEACHER, etc.
    private Boolean status = true;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt = LocalDateTime.now();
}
