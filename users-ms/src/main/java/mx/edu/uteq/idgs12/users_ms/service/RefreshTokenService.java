package mx.edu.uteq.idgs12.users_ms.service;

import mx.edu.uteq.idgs12.users_ms.entity.RefreshToken;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString()); // token único
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // expira en 7 días
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
