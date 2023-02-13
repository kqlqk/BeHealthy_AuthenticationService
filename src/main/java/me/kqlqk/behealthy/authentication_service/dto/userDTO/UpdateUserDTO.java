package me.kqlqk.behealthy.authentication_service.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UpdateUserDTO {
    private long id;

    @Pattern(regexp = "[a-zA-Z]+", message = "Name should contains only letters")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @NotEmpty(message = "Name cannot be null")
    private String name;

    @Pattern(regexp = "^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$", message = "Email should be valid")
    @NotEmpty(message = "Email cannot be null")
    private String email;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$",
            message = "Password should be between 8 and 50 characters, no spaces, at least: 1 number, 1 uppercase letter, 1 lowercase letter")
    @NotEmpty(message = "Password cannot be null")
    private String password;
}
