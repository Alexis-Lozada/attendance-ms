package mx.edu.uteq.idgs12.academic_ms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "courses")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCourse;

    @ManyToOne
    @JoinColumn(name = "idUniversity", nullable = false)
    private University university;

    @ManyToOne
    @JoinColumn(name = "idDivision", nullable = true) // Si es null, significa que es un curso general
    private Division division;

    @Column(length = 20, nullable = false)
    private String courseCode;

    @Column(length = 150, nullable = false)
    private String courseName;

    @Column(length = 10, nullable = true) // Si es null, significa que es un curso general
    private String semester;

    @Column(nullable = false)
    private Boolean status = true;
}
