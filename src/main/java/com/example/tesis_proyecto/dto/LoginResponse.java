package com.example.tesis_proyecto.dto;

import com.example.tesis_proyecto.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private UserDTO user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = new UserDTO(user);
    }
}
