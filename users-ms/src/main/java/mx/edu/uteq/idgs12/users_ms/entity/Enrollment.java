package mx.edu.uteq.idgs12.users_ms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment")
@Data
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnrollment;

    @ManyToOne
    @JoinColumn(name = "idStudent", nullable = false)
    private User student;

    private Integer idGroup;

    private LocalDate enrollmentDate = LocalDate.now();

    private Boolean status = true;
}
