package mx.edu.uteq.idgs12.attendance_ms.dto.schedule;

import lombok.Data;
import java.util.List;

@Data
public class ScheduleCreateRequest {
    private Integer idGroup;
    private Integer idCourse;
    private Integer idProfessor;
    private List<ScheduleDTO> schedules; // Lista de horarios (crear/actualizar/eliminar)
}
