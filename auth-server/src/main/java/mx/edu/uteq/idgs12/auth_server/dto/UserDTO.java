package mx.edu.uteq.idgs12.auth_server.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer idUser;
    private Integer idUniversity;
    private Integer idDivision;
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
