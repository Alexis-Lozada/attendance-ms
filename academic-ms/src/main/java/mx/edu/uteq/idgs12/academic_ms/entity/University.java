package mx.edu.uteq.idgs12.academic_ms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUniversity;

    @Column(length = 50, nullable = false, unique = true)
    private String code;

    @Column(length = 150, nullable = false)
    private String name;

    @Column(length = 100)
    private String campus;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String logo;

    @Column(length = 100)
    private String email;

    private Boolean status = true;
}
