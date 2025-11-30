package mx.edu.uteq.idgs12.users_ms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()

                        // TODO lo demás requiere token OAuth
                        .anyRequest().authenticated()
                )
                // Activar Resource Server (valida JWT de auth-server)
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwkSetUri("http://34.28.53.159:9000/oauth2/jwks"))
                );

        return http.build();
    }
}
