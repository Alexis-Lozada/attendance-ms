package mx.edu.uteq.idgs12.attendance_ms.dto;

import lombok.Data;

@Data
public class GroupCourseDTO {
    private Integer idGroupCourse;
    private Integer idGroup;
    private String groupCode;
    private Integer idCourse;
    private String courseCode;
    private String courseName;
    private Integer idProfessor;
    private String professorName;
}
