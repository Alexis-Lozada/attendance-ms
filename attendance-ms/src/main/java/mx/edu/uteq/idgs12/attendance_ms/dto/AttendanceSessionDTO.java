package mx.edu.uteq.idgs12.attendance_ms.dto;

import lombok.Data;

@Data
public class AttendanceSessionDTO {
    private Integer idGroupCourse;
    private Integer idProfessor;
    private Double geoLatitude;
    private Double geoLongitude;
}
