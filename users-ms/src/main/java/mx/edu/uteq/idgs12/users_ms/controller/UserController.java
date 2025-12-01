package mx.edu.uteq.idgs12.users_ms.controller;

import mx.edu.uteq.idgs12.users_ms.dto.ChangePasswordDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** Obtener usuario por ID */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crear nuevo usuario */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRegisterDTO dto) {
        try {
            UserResponseDTO created = userService.createUser(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Actualizar información de un usuario */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Integer id,
            @RequestBody UserRegisterDTO dto
    ) {
        return userService.updateUser(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Cambiar el estado (activo/inactivo) de un usuario */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            UserResponseDTO updated = userService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Obtener todos los usuarios de una universidad */
    @GetMapping("/university/{idUniversity}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByUniversity(
            @PathVariable Integer idUniversity,
            @RequestParam(required = false) Boolean active
    ) {
        List<UserResponseDTO> users = userService.getUsersByUniversity(idUniversity, active);
        return ResponseEntity.ok(users);
    }

    /** Cambiar contraseña de un usuario */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordDTO dto) {
        boolean changed = userService.changePassword(id, dto);
        if (changed) {
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid current password or user not found"));
        }
    }
}
