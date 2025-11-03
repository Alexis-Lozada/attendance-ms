package mx.edu.uteq.idgs12.users_ms.service;

import mx.edu.uteq.idgs12.users_ms.dto.ChangePasswordDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserLoginDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.entity.RefreshToken;
import mx.edu.uteq.idgs12.users_ms.repository.UserRepository;
import mx.edu.uteq.idgs12.users_ms.security.JwtUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    /** Registro de usuario */
    public UserResponseDTO register(UserRegisterDTO dto) {
        User user = new User();
        user.setIdUniversity(dto.getIdUniversity());
        user.setEmail(dto.getEmail());
        user.setEnrollmentNumber(dto.getEnrollmentNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole());
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    /** Login -> devuelve accessToken + refreshToken */
    public Optional<Map<String, Object>> login(UserLoginDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                // Generar Access Token (corto plazo)
                String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

                // Generar Refresh Token (cada login crea uno nuevo, no borra los viejos)
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                Map<String, Object> response = new HashMap<>();
                response.put("user", mapToResponse(user));
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken.getToken());

                return Optional.of(response);
            }
        }
        return Optional.empty();
    }

    /** Refrescar Access Token usando Refresh Token válido (con rotación) */
    public Optional<Map<String, Object>> refreshAccessToken(String refreshTokenStr) {
        return refreshTokenService.findByToken(refreshTokenStr)
                .filter(token -> !refreshTokenService.isExpired(token))
                .map(token -> {
                    User user = token.getUser();
                
                    // Invalida el refresh token viejo
                    refreshTokenService.delete(token);
                
                    // Genera uno nuevo
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                
                    // Nuevo access token
                    String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
                
                    Map<String, Object> response = new HashMap<>();
                    response.put("accessToken", newAccessToken);
                    response.put("refreshToken", newRefreshToken.getToken());
                
                    return response;
                });
    }

    /** Logout -> elimina solo un refresh token */
    public boolean logout(String refreshTokenStr) {
        return refreshTokenService.findByToken(refreshTokenStr)
                .map(token -> {
                    refreshTokenService.delete(token);
                    return true;
                }).orElse(false);
    }

    public Optional<UserResponseDTO> getUserById(Integer id) {
        return userRepository.findById(id).map(this::mapToResponse);
    }

    public Optional<UserResponseDTO> updateUser(Integer id, UserRegisterDTO dto) {
        return userRepository.findById(id).map(user -> {
            if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) user.setLastName(dto.getLastName());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getEnrollmentNumber() != null) user.setEnrollmentNumber(dto.getEnrollmentNumber());
            if (dto.getProfileImage() != null) user.setProfileImage(dto.getProfileImage());
            User updated = userRepository.save(user);
            return mapToResponse(updated);
        });
    }

    public List<UserResponseDTO> getUsersByUniversity(Integer idUniversity) {
    return userRepository.findByIdUniversity(idUniversity).stream()
            .filter(u -> !u.getStatus().equals(false))
            .map(this::mapToResponse)
            .toList();
    }

    public boolean changePassword(Integer userId, ChangePasswordDTO dto) {
        return userRepository.findById(userId).map(user -> {
            // Verificar la contraseña actual
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return false;
            }
            // Guardar la nueva contraseña
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    /** Convertir Entity -> DTO */
    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}