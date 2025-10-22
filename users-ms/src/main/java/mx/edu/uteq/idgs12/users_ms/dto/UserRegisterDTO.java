package mx.edu.uteq.idgs12.users_ms.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private Integer idUniversity;
    private String email;
    private String enrollmentNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String role;
}
