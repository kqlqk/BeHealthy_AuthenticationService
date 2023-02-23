package me.kqlqk.behealthy.authentication_service.dto.user_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kqlqk.behealthy.authentication_service.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDTO {
    private long id;
    private String name;
    private String email;
    private String password;

    public static GetUserDTO convert(User user) {
        return new GetUserDTO(user.getId(), user.getName(), user.getEmail(), user.getPassword());
    }
}
