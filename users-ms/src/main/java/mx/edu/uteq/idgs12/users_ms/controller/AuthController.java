package mx.edu.uteq.idgs12.users_ms.controller;

import mx.edu.uteq.idgs12.users_ms.dto.UserLoginDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** Registro de usuario */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        UserResponseDTO response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    /** Login -> devuelve accessToken + refreshToken */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        Optional<Map<String, Object>> response = authService.login(dto);
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }

    /** Refrescar Access Token usando Refresh Token válido */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return authService.refreshAccessToken(refreshToken)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(403)
                        .body(Map.of("error", "Invalid or expired refresh token")));
    }

    /** Logout -> elimina un refresh token específico */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        boolean loggedOut = authService.logout(refreshToken);
        if (loggedOut) {
            return ResponseEntity.ok(Map.of("message", "User logged out successfully"));
        } else {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token"));
        }
    }
}
