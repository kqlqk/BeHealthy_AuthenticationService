package me.kqlqk.behealthy.authentication_service.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckPasswordDTO {
    @NotEmpty(message = "Password cannot be null")
    private String password;
}
