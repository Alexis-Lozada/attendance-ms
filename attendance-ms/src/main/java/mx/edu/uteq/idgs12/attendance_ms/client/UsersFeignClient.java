package mx.edu.uteq.idgs12.attendance_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "users-ms", contextId = "usersFeignClient")
public interface UsersFeignClient {

    @GetMapping("/api/user/{idUser}")
    Map<String, Object> getUserById(@PathVariable("idUser") Integer idUser);
}
