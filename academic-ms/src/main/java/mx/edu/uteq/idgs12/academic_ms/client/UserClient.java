package mx.edu.uteq.idgs12.academic_ms.client;

import mx.edu.uteq.idgs12.academic_ms.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms")
public interface UserClient {

    @GetMapping("/api/user/{id}")
    UserDTO getUserById(@PathVariable("id") Integer id);

    @GetMapping("/api/enrollments/group/{idGroup}/count")
    Long getEnrollmentCountByGroup(@PathVariable("idGroup") Integer idGroup);
}
