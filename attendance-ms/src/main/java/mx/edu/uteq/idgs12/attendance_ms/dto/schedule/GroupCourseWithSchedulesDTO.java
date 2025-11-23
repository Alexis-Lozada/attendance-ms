package mx.edu.uteq.idgs12.attendance_ms.dto.schedule;

import lombok.Data;
import java.util.List;

@Data
public class GroupCourseWithSchedulesDTO {
    private Integer idGroupCourse;
    private Integer idGroup;
    private Integer idCourse;
    private Integer idProfessor;
    private List<ScheduleDTO> schedules;
}
