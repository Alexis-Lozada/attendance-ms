package mx.edu.uteq.idgs12.users_ms.dto;

import lombok.Data;

/**
 * DTO para la transferencia de datos de Group (usado como entrada y como base de la entidad).
 */
@Data
public class GroupDTO {
    private Integer idGroup;
    
    // Referencias a FK
    private Integer idProgram;
    private Long idTutor;
    
    // Campos propios del Group
    private String groupCode;
    private String groupName;
    private String semester;
    private String academicYear;
    private Boolean status;
    
    // Campos ENRIQUECIDOS desde otros microservicios
    private String programName;    // Enriquecido desde academic-ms
    private String tutorName;      // Enriquecido desde users-ms (propio)
}