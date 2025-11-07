package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;

@Data
public class DivisionDTO {
    private Integer idDivision;
    private Integer idUniversity;
    private Integer idCoordinator;
    private String code;
    private String name;
    private String description;
    private Boolean status;
}
