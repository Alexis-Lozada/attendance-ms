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
@Table(name = "division")
@Data
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDivision;

    @ManyToOne
    @JoinColumn(name = "idUniversity", nullable = false)
    private University university;

    @Column(nullable = true)
    private Integer idCoordinator;

    @Column(length = 20, nullable = false)
    private String code;

    @Column(length = 150, nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean status = true;
}