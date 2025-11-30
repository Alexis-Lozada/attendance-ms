package mx.edu.uteq.idgs12.users_ms.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignAuthForwardConfig {

    @Bean
    public RequestInterceptor bearerTokenForwarder() {
        return template -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                String tokenValue = jwtAuth.getToken().getTokenValue();
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
            }
        };
    }
}
