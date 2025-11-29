package mx.edu.uteq.idgs12.auth_server.controller;

import mx.edu.uteq.idgs12.auth_server.dto.AuthResponse;
import mx.edu.uteq.idgs12.auth_server.dto.LoginRequest;
import mx.edu.uteq.idgs12.auth_server.dto.RefreshRequest;
import mx.edu.uteq.idgs12.auth_server.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse res = authService.login(req);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
        try {
            AuthResponse res = authService.refresh(req);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
        }
    }
}
