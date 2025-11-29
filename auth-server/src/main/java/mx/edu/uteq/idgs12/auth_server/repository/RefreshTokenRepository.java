package mx.edu.uteq.idgs12.auth_server.repository;

import mx.edu.uteq.idgs12.auth_server.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
