package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Integer idCourse;
    private Integer idUniversity;
    private Integer idDivision;
    private String divisionCode;
    private String divisionName;
    private String courseCode;
    private String courseName;
    private String semester;
    private Boolean status;
    private Long modulesCount;
    private Long groupsCount;
}
