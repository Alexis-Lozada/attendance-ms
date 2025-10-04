package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;

@Data
public class UniversityDTO {
    private Integer idUniversity;
    private String code;
    private String name;
    private String campus;
    private String address;
    private String logo;
    private String email;
    private Boolean status;
}
