package mx.edu.uteq.idgs12.auth_server.service;

import mx.edu.uteq.idgs12.auth_server.dto.AuthResponse;
import mx.edu.uteq.idgs12.auth_server.dto.LoginRequest;
import mx.edu.uteq.idgs12.auth_server.dto.RefreshRequest;
import mx.edu.uteq.idgs12.auth_server.entity.RefreshToken;
import mx.edu.uteq.idgs12.auth_server.entity.User;
import mx.edu.uteq.idgs12.auth_server.repository.RefreshTokenRepository;
import mx.edu.uteq.idgs12.auth_server.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.access-ttl-seconds:3600}")
    private long accessTtlSeconds;

    @Value("${app.jwt.refresh-ttl-days:7}")
    private long refreshTtlDays;

    @Value("${app.jwt.issuer:http://localhost:9000}")
    private String issuer;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (user.getStatus() != null && !user.getStatus()) {
            throw new RuntimeException("User disabled");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user).getToken();

        return new AuthResponse(accessToken, refreshToken, "Bearer", accessTtlSeconds);
    }

    public AuthResponse refresh(RefreshRequest req) {
        RefreshToken stored = refreshTokenRepository.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (Boolean.TRUE.equals(stored.getRevoked()) || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Rotación: invalida el viejo y crea uno nuevo
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        String newAccessToken = createAccessToken(user);
        String newRefreshToken = createRefreshToken(user).getToken();

        return new AuthResponse(newAccessToken, newRefreshToken, "Bearer", accessTtlSeconds);
    }

    private String createAccessToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTtlSeconds))
                .claim("uid", user.getIdUser())
                .claim("role", user.getRole())
                // opcional, por si luego quieres authorities por scope:
                .claim("scope", "read write")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(LocalDateTime.now().plusDays(refreshTtlDays));
        rt.setRevoked(false);
        return refreshTokenRepository.save(rt);
    }
}
