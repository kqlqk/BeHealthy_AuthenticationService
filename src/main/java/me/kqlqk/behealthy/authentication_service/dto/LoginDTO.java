package me.kqlqk.behealthy.authentication_service.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginDTO {
    @NotEmpty(message = "Email cannot be null")
    private String email;

    @NotEmpty(message = "Password cannot be null")
    private String password;
}
