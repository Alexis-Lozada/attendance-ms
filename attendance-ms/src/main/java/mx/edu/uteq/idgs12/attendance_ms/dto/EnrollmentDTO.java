package mx.edu.uteq.idgs12.attendance_ms.dto;

import lombok.Data;

@Data
public class EnrollmentDTO {
    private Integer idEnrollment;
    private Integer idStudent;
    private String studentName;
    private String studentEmail;
    private Integer idGroup;
    private String groupCode;
    private Boolean status;
}
