package integration.authentication_service.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserException;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void create_shouldCreateUserAndAddToDb() {
        UserDTO userDTO = new UserDTO("steve", "steve@mail.com", "Password12345");

        userService.create(userDTO);

        User user = userService.getByEmail("steve@mail.com");

        assertThat(user).isNotNull();
    }

    @Test
    public void create_shouldThrowException() {
        String invalidName = "d";
        UserDTO userDTO = new UserDTO(invalidName, "steve@mail.com", "Password12345");

        assertThrows(UserException.class, () -> userService.create(userDTO));


        String invalidEmail = "badEmail";
        UserDTO userDTO2 = new UserDTO("steve", invalidEmail, "Password12345");

        assertThrows(UserException.class, () -> userService.create(userDTO2));


        String invalidPassword = "bad_pswd";
        UserDTO userDTO3 = new UserDTO("steve", "steve@mail.com", invalidPassword);

        assertThrows(UserException.class, () -> userService.create(userDTO3));


        UserDTO userDTO4 = new UserDTO(null, "steve@mail.com", "Password12345");

        assertThrows(UserException.class, () -> userService.create(userDTO4));


        UserDTO userDTO5 = new UserDTO("steve", null, "Password12345");

        assertThrows(UserException.class, () -> userService.create(userDTO5));


        UserDTO userDTO6 = new UserDTO("steve", "steve@mail.com", null);

        assertThrows(UserException.class, () -> userService.create(userDTO6));
    }

    @Test
    public void update_shouldUpdateUser() {
        String newName = "newName";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setName(newName);

        userService.update(userDTO);

        assertThat(userService.getById(1).getName()).isEqualTo(newName);


        String newEmail = "newEmail@gmail.com";
        userDTO.setEmail(newEmail);

        userService.update(userDTO);

        assertThat(userService.getById(1).getEmail()).isEqualTo(newEmail.toLowerCase());


        String newPassword = "newPSWD12345";
        userDTO.setPassword(newPassword);

        userService.update(userDTO);

        assertThat(passwordEncoder.matches(newPassword, userService.getById(1).getPassword())).isTrue();
    }

    @Test
    public void update_shouldThrowException() {
        String newInvalidName = "d";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setName(newInvalidName);

        assertThrows(UserException.class, () -> userService.update(userDTO));


        String newInvalidEmail = "badEmail";
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(1);
        userDTO2.setEmail(newInvalidEmail);

        assertThrows(UserException.class, () -> userService.update(userDTO2));


        String newInvalidPassword = "badPswd";
        UserDTO userDTO3 = new UserDTO();
        userDTO3.setId(1);
        userDTO3.setPassword(newInvalidPassword);

        assertThrows(UserException.class, () -> userService.update(userDTO3));
    }
}
