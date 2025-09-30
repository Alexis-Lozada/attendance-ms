package mx.edu.uteq.idgs12.microservicio_division.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nombre;
}