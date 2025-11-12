package mx.edu.uteq.idgs12.academic_ms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCourse;

    @ManyToOne
    @JoinColumn(name = "idUniversity", nullable = false)
    private University university;

    @ManyToOne
    @JoinColumn(name = "idDivision", nullable = true)
    private Division division;

    @Column(length = 20, nullable = false)
    private String courseCode;

    @Column(length = 150, nullable = false)
    private String courseName;

    /** Si es null, significa que es un curso general (aplicable a todos los semestres) */
    @Column(length = 10, nullable = true)
    private String semester;

    @Column(nullable = false)
    private Boolean status = true;
}
