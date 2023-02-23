package me.kqlqk.behealthy.authentication_service.dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    @NotEmpty(message = "Email cannot be null")
    private String email;

    @NotEmpty(message = "Password cannot be null")
    private String password;
}
