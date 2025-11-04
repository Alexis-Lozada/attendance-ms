package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;

@Data
public class GroupDTO {
    private Integer idGroup;
    private Integer idProgram;
    private String programName; // Nombre del programa educativo
    private Integer idTutor;
    private String tutorName;   // Nombre del tutor
    private String groupCode;
    private String groupName;
    private String semester;
    private String academicYear;
    private Boolean status;
}
