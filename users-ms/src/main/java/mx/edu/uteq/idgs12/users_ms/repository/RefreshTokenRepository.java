package mx.edu.uteq.idgs12.users_ms.repository;

import mx.edu.uteq.idgs12.users_ms.entity.RefreshToken;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
