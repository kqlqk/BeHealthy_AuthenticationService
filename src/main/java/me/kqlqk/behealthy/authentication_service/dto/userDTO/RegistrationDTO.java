package me.kqlqk.behealthy.authentication_service.dto.userDTO;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    @Pattern(regexp = "[a-zA-Z]+", message = "Name should contains only letters")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @NotEmpty(message = "Name cannot be null")
    private String name;

    @JsonView(UserDTO.WithoutPassword.class)
    @Pattern(regexp = "^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$", message = "Email should be valid")
    @NotEmpty(message = "Email cannot be null")
    private String email;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$",
            message = "Password should be between 8 and 50 characters, no spaces, at least: 1 number, 1 uppercase letter, 1 lowercase letter")
    @NotEmpty(message = "Password cannot be null")
    private String password;
}
