package mx.edu.uteq.idgs12.academic_ms.client;

import mx.edu.uteq.idgs12.academic_ms.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms", url = "${USERS_MS_URL}")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Integer id);
}
