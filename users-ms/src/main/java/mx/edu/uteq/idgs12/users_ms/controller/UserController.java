package mx.edu.uteq.idgs12.users_ms.controller;

import mx.edu.uteq.idgs12.users_ms.dto.UserLoginDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Registro de usuario */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    /** Login -> devuelve accessToken + refreshToken */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        Optional<Map<String, Object>> response = userService.login(dto);
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    /** Refrescar Access Token */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return userService.refreshAccessToken(refreshToken)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(403).body(Map.of("error", "Invalid or expired refresh token")));
    }

    /** Logout -> elimina un refresh token específico */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        boolean loggedOut = userService.logout(refreshToken);
        if (loggedOut) {
            return ResponseEntity.ok("User logged out successfully");
        } else {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/university/{idUniversity}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByUniversity(@PathVariable Integer idUniversity) {
        List<UserResponseDTO> users = userService.getUsersByUniversity(idUniversity);
        return ResponseEntity.ok(users);
    }
}
