package mx.edu.uteq.idgs12.users_ms.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mx.edu.uteq.idgs12.users_ms.dto.UserCrudDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserLoginDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.entity.RefreshToken;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.repository.UserRepository;
import mx.edu.uteq.idgs12.users_ms.security.JwtUtil;

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

    public List<UserResponseDTO> getUsersByUniversity(Integer idUniversity) {
    return userRepository.findByIdUniversity(idUniversity).stream()
            .filter(u -> !u.getStatus().equals(false))
            .map(this::mapToResponse)
            .toList();
    }

        // ============================ CRUD OPERATIONS ============================
    
    /** Obtener todos los usuarios */
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Obtener usuario por ID */
    public Optional<UserResponseDTO> findUserById(Integer id) {
        return userRepository.findById(id)
                .map(this::mapToResponse);
    }

    /** Crear usuario (CRUD) */
    public UserResponseDTO createUser(UserCrudDTO dto) {
        User user = new User();
        user.setIdUniversity(dto.getIdUniversity());
        user.setEmail(dto.getEmail());
        user.setEnrollmentNumber(dto.getEnrollmentNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    /** Actualizar usuario completo */
    public Optional<UserResponseDTO> updateUser(Integer id, UserCrudDTO dto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setIdUniversity(dto.getIdUniversity());
                    existingUser.setEmail(dto.getEmail());
                    existingUser.setEnrollmentNumber(dto.getEnrollmentNumber());
                    
                    // Solo actualizar password si se proporciona uno nuevo
                    if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }
                    
                    existingUser.setFirstName(dto.getFirstName());
                    existingUser.setLastName(dto.getLastName());
                    existingUser.setRole(dto.getRole());
                    existingUser.setStatus(dto.getStatus());

                    User updated = userRepository.save(existingUser);
                    return mapToResponse(updated);
                });
    }

    /** Actualizar solo el estado del usuario */
    public Optional<UserResponseDTO> updateUserStatus(Integer id, Boolean status) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    User updated = userRepository.save(user);
                    return mapToResponse(updated);
                });
    }

    /** Eliminar usuario */
    public boolean deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /** Buscar usuario por email */
    public Optional<UserResponseDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToResponse);
    }

    // ============================ COMMON METHODS ============================
    
    /** Convertir Entity -> DTO */
    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setIdUser(user.getIdUser());
        dto.setIdUniversity(user.getIdUniversity());
        dto.setEmail(user.getEmail());
        dto.setEnrollmentNumber(user.getEnrollmentNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }
}