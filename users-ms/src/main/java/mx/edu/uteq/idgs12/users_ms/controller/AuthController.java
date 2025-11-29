package mx.edu.uteq.idgs12.users_ms.controller;

import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
