package mx.edu.uteq.idgs12.academic_ms.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CourseModuleDTO {
    private Integer idModule;
    private Integer idCourse;
    private String courseName;
    private Integer moduleNumber;
    private String title;
    private Date startDate;
    private Date endDate;
}