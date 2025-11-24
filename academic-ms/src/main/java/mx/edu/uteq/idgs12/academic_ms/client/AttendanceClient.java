package mx.edu.uteq.idgs12.academic_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "attendance-ms")
public interface AttendanceClient {

    /* Devuelve el número de grupos relacionados a un curso */
    @GetMapping("/api/group-courses/course/{idCourse}/count")
    Long getGroupsCountByCourse(@PathVariable("idCourse") Integer idCourse);
}
