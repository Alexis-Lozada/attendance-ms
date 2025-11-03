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
@Table(name = "programs")
@Data
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProgram;

    @ManyToOne
    @JoinColumn(name = "idDivision", nullable = false)
    private Division division;

    @Column(length = 50, nullable = false)
    private String programCode;

    @Column(length = 200, nullable = false)
    private String programName;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean status = true;
}