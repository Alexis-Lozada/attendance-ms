package mx.edu.uteq.idgs12.attendance_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "academic-ms")
public interface AcademicFeignClient {

    @GetMapping("/api/groups/{idGroup}")
    Map<String, Object> getGroupById(@PathVariable("idGroup") Integer idGroup);

    @GetMapping("/api/courses/{idCourse}")
    Map<String, Object> getCourseById(@PathVariable("idCourse") Integer idCourse);
}
