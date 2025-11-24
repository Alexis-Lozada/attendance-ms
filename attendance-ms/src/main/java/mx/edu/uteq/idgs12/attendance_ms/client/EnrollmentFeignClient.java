package mx.edu.uteq.idgs12.attendance_ms.client;

import mx.edu.uteq.idgs12.attendance_ms.dto.EnrollmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "users-ms", contextId = "enrollmentFeignClient")
public interface EnrollmentFeignClient {

    @GetMapping("/api/enrollments/group/{idGroup}")
    List<EnrollmentDTO> getEnrollmentsByGroup(@PathVariable("idGroup") Integer idGroup);
}
