package mx.edu.uteq.idgs12.auth_server.service;

import mx.edu.uteq.idgs12.auth_server.dto.AuthResponse;
import mx.edu.uteq.idgs12.auth_server.dto.LoginRequest;
import mx.edu.uteq.idgs12.auth_server.dto.RefreshRequest;
import mx.edu.uteq.idgs12.auth_server.dto.UserDTO;
import mx.edu.uteq.idgs12.auth_server.entity.RefreshToken;
import mx.edu.uteq.idgs12.auth_server.entity.User;
import mx.edu.uteq.idgs12.auth_server.repository.RefreshTokenRepository;
import mx.edu.uteq.idgs12.auth_server.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Value("${app.jwt.access-ttl-seconds:3600}")
    private long accessTtlSeconds;

    @Value("${app.jwt.refresh-ttl-days:30}")
    private long refreshTtlDays;

    @Value("${app.jwt.issuer:http://localhost:9000}")
    private String issuer;

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

        return new AuthResponse(toUserDTO(user), accessToken, refreshToken);
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

        return new AuthResponse(toUserDTO(user), newAccessToken, newRefreshToken);
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

    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto); // UserDTO no incluye password
        return dto;
    }
}
