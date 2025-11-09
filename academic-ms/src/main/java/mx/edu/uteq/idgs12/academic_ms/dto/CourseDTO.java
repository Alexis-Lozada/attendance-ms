package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Integer idCourse;
    private Integer idUniversity;
    private String universityName;
    private Integer idDivision;
    private String divisionName;
    private String code;
    private String name;
    private String semester;
    private Boolean status;
}