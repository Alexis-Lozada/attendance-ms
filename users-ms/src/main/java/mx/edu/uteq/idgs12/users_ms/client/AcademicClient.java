package mx.edu.uteq.idgs12.users_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "academic-ms")
public interface AcademicClient {
    @GetMapping("/api/groups/{id}")
    Map<String, Object> getGroupById(@PathVariable("id") Integer id);
}
