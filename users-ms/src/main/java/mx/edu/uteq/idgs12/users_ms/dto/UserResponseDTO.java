package mx.edu.uteq.idgs12.users_ms.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer idUser;
    private String email;
    private String enrollmentNumber;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean status;
}
