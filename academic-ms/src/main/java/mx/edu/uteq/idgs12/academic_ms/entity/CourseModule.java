package mx.edu.uteq.idgs12.academic_ms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "course_modules")
@Data
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModule;

    @ManyToOne
    @JoinColumn(name = "idCourse", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer moduleNumber;

    @Column(length = 150, nullable = false)
    private String title;

    private LocalDate startDate;
    private LocalDate endDate;
}
