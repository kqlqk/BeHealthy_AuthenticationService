package me.kqlqk.behealthy.authentication_service.dto.user_dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckPasswordDTO {
    @NotEmpty(message = "Password cannot be null")
    private String password;
}
