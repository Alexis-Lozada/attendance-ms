package mx.edu.uteq.idgs12.academic_ms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "groups")
@Data
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGroup;

    @ManyToOne
    @JoinColumn(name = "idProgram", nullable = false)
    private Program program;

    @Column(nullable = false)
    private Integer idTutor;

    @Column(length = 20, nullable = false)
    private String groupCode;

    @Column(length = 150, nullable = false)
    private String groupName;

    @Column(length = 20)
    private String semester;

    @Column(length = 20)
    private String academicYear;

    private Boolean status = true;
}
