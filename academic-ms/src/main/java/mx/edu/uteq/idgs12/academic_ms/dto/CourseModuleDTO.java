package mx.edu.uteq.idgs12.academic_ms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CourseModuleDTO {
    private Integer idModule;
    private Integer idCourse;
    private Integer moduleNumber;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}
