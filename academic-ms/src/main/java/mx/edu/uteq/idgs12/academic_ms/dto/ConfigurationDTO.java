package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;

@Data
public class ConfigurationDTO {
    private Integer idConfiguration;
    private Integer idUniversity;
    private String parameterName;
    private String parameterValue;
    private String description;
}
