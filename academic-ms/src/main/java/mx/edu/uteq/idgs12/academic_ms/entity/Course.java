package mx.edu.uteq.idgs12.academic_ms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @JoinColumn(name = "idDivision", nullable = false)
    private Division division;

    @Column(length = 20, nullable = false, unique = true)
    private String courseCode;

    @Column(length = 100, nullable = false)
    private String courseName;

    @Column(length = 20)
    private String semester;

    private Boolean status = true;
}