package me.kqlqk.behealthy.authentication_service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kqlqk.behealthy.authentication_service.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    public interface WithoutPassword {
    }

    @JsonView(WithoutPassword.class)
    private long id;

    @JsonView(WithoutPassword.class)
    @Pattern(regexp = "[a-zA-Z]+", message = "Name should contains only letters")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @NotEmpty(message = "Name cannot be null")
    private String name;

    @JsonView(WithoutPassword.class)
    @Pattern(regexp = "^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$", message = "Email should be valid")
    @NotEmpty(message = "Email cannot be null")
    private String email;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$",
            message = "Password should be between 8 and 50 characters, no spaces, at least: 1 number, 1 uppercase letter, 1 lowercase letter")
    @NotEmpty(message = "Password cannot be null")
    private String password;

    public UserDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static List<UserDTO> convertListOfUsersToListOfUserDTOs(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getName());
            userDTO.setPassword(user.getPassword());
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }

    public static UserDTO convertFromUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPassword(user.getPassword());

        return userDTO;
    }
}
