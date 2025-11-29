package mx.edu.uteq.idgs12.users_ms.service;

import mx.edu.uteq.idgs12.users_ms.dto.UserRegisterDTO;
import mx.edu.uteq.idgs12.users_ms.dto.UserResponseDTO;
import mx.edu.uteq.idgs12.users_ms.entity.User;
import mx.edu.uteq.idgs12.users_ms.repository.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    /** Convertir Entity -> DTO */
    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
