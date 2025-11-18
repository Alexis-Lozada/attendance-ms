package mx.edu.uteq.idgs12.attendance_ms.dto.schedule;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Integer idSchedule;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String classroom;
    private Integer idGroupCourse;
}
