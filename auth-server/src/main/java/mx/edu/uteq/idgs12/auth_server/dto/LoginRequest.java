package mx.edu.uteq.idgs12.auth_server.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
