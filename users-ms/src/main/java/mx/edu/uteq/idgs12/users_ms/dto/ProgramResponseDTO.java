package mx.edu.uteq.idgs12.users_ms.dto;

import lombok.Data;

@Data
public class ProgramResponseDTO {
    private Integer idProgram;
    private Integer idDivision;
    private String programCode;
    private String programName;
    private String description;
    private Boolean status;
}