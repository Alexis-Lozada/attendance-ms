package mx.edu.uteq.idgs12.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration cfg = new CorsConfiguration();

    cfg.setAllowCredentials(false);              // ✅ clave
    cfg.addAllowedOriginPattern("*");            // o cfg.setAllowedOrigins(List.of("*"));
    cfg.addAllowedMethod(CorsConfiguration.ALL); // "*"
    cfg.addAllowedHeader(CorsConfiguration.ALL); // "*"
    cfg.addExposedHeader("Authorization");
    cfg.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return new CorsFilter(source);
  }
}
