package mx.edu.uteq.idgs12.users_ms.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "academic-ms", url = "http://localhost:8082/api/programs")
public interface ProgramClient {
    @GetMapping("/{id}")
    Map<String, Object> getProgramById(@PathVariable("id") Integer id);
}