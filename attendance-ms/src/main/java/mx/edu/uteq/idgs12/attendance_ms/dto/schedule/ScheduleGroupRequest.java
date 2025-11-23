package mx.edu.uteq.idgs12.attendance_ms.dto.schedule;

import lombok.Data;
import java.util.List;

@Data
public class ScheduleGroupRequest {
    private Integer idGroup; // Grupo al que pertenecen todos los horarios
    private List<ScheduleCreateRequest> groupCourses; // Materias con sus horarios
}
