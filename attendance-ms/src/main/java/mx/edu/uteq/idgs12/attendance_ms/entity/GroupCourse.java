package mx.edu.uteq.idgs12.attendance_ms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "group_courses")
@Data
public class GroupCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGroupCourse;

    @Column(nullable = false)
    private Integer idGroup;

    @Column(nullable = false)
    private Integer idCourse;

    @Column(nullable = false)
    private Integer idProfessor;
}
