package mx.edu.uteq.idgs12.academic_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "attendance-ms", url = "${ATTENDANCE_MS_URL:http://localhost:8083}")
public interface AttendanceClient {

    /** Devuelve los IDs de grupos asociados a un curso */
    @GetMapping("/api/group-courses/course/{idCourse}/groups")
    List<Integer> getGroupIdsByCourse(@PathVariable("idCourse") Integer idCourse);
}
