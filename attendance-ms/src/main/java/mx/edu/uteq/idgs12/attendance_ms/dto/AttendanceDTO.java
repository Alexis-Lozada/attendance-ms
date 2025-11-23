package mx.edu.uteq.idgs12.attendance_ms.dto;

import lombok.Data;

@Data
public class AttendanceDTO {
    private Integer idAttendance;
    private Integer idSchedule;
    private Integer idStudent;
    private String attendanceDate;
    private String status;
    private String comments;

    // Datos del estudiante
    private String studentName;
    private String enrollmentNumber;
    private String profileImage;
}

