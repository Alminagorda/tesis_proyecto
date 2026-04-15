package com.example.tesis_proyecto.dto;

import com.example.tesis_proyecto.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String id;
    private String email;
    private String fullName;
    private String role;

    public UserDTO(User user) {
        this.id = user.getId().toString();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.role = user.getRole();
    }
}
