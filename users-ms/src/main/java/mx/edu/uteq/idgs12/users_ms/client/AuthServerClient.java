package mx.edu.uteq.idgs12.users_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import feign.Headers;

@FeignClient(name = "auth-server", url = "${AUTH_SERVER_URL:http://localhost:9000}")
public interface AuthServerClient {

    @PostMapping("/oauth2/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String requestClientToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret
    );
}
