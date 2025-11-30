package mx.edu.uteq.idgs12.chat_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "users-ms")
public interface UserClient {

    @GetMapping("/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);
}
