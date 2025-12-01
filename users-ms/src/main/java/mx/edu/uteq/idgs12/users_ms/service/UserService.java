package mx.edu.uteq.idgs12.users_ms.service;

import mx.edu.uteq.idgs12.users_ms.dto.ChangePasswordDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** Obtener usuario por ID */
    public Optional<UserResponseDTO> getUserById(Integer id) {
        return userRepository.findById(id).map(this::mapToResponse);
    }

    /** Crear nuevo usuario */
    public UserResponseDTO createUser(UserRegisterDTO dto) {

        // Validar email duplicado
        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email ya registrado");
        });

        // Validar matrícula duplicada
        if (dto.getEnrollmentNumber() != null) {
            userRepository.findByEnrollmentNumber(dto.getEnrollmentNumber()).ifPresent(u -> {
                throw new RuntimeException("Matrícula ya registrada");
            });
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    /** Actualizar datos de usuario */
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

    /** Cambiar el estado (activo/inactivo) de un usuario */
    public UserResponseDTO updateStatus(Integer id, Boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        user.setStatus(status);
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    /** Obtener todos los usuarios de una universidad */
    public List<UserResponseDTO> getUsersByUniversity(Integer idUniversity, Boolean onlyActive) {
        return userRepository.findByIdUniversity(idUniversity).stream()
                .filter(u -> onlyActive == null || !onlyActive || Boolean.TRUE.equals(u.getStatus()))
                .map(this::mapToResponse)
                .toList();
    }

    /** Cambiar contraseña */
    public boolean changePassword(Integer userId, ChangePasswordDTO dto) {
        return userRepository.findById(userId).map(user -> {
            // Verificar contraseña actual
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return false;
            }
            // Guardar nueva contraseña
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
