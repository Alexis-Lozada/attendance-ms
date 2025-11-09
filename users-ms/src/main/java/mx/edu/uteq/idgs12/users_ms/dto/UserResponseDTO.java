package mx.edu.uteq.idgs12.users_ms.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer idUser;
    private Integer idUniversity;
    private String email;
    private String enrollmentNumber;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String role;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
