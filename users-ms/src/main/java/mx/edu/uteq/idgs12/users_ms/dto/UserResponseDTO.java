package mx.edu.uteq.idgs12.users_ms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Integer idUser;
    private Integer idUniversity;
    private Integer idDivision;
    private Integer idProgram;
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
