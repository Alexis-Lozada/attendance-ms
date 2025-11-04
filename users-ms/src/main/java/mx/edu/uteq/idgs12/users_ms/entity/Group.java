package mx.edu.uteq.idgs12.users_ms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la tabla 'GROUP'.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GROUPS") // Se usa GROUPS para evitar conflicto con palabra reservada SQL
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGroup;

    @Column(name = "idProgram", nullable = false)
    private Integer idProgram; // FK: idProgram (Referencia a academic-ms)

    @Column(name = "idTutor", nullable = false)
    private Long idTutor; // FK: idTutor (Referencia a USER dentro de users-ms)

    @Column(length = 20, nullable = false)
    private String groupCode;

    @Column(length = 100, nullable = false)
    private String groupName;

    @Column(length = 50, nullable = false)
    private String semester;

    @Column(length = 4, nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private Boolean status = true;
}