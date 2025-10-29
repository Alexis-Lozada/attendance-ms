package mx.edu.uteq.idgs12.users_ms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(nullable = true)
    private Integer idUniversity;

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

    private String role; // ADMIN, STUDENT, TEACHER, TUTOR.
    private Boolean status = true;

    private LocalDateTime lastLogin;
    private LocalDateTime createdAt = LocalDateTime.now();
}
