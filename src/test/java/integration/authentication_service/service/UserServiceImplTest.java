package integration.authentication_service.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
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
        int size = userService.getAll().size();

        userService.create(new User("steve", "steve@mail.com", "Password12345"));

        int newSize = userService.getAll().size();

        assertThat(newSize).isEqualTo(size + 1);
    }

    @Test
    public void create_shouldThrowException() {
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(new User("name", "user1@mail.com", "Pswd1234")));
    }

    @Test
    public void update_shouldUpdateUser() {
        User user = userService.getById(1);
        user.setName("newName");
        user.setEmail("newMail@mail.com");
        user.setPassword("newPswd123");

        userService.update(user);

        assertThat(userService.getById(1).getName()).isEqualTo(user.getName());
        assertThat(userService.getById(1).getEmail()).isEqualTo(user.getEmail());
        assertThat(userService.getById(1).getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    public void update_shouldThrowException() {
        User user = new User();
        user.setId(999);

        assertThrows(UserNotFoundException.class, () -> userService.update(user));
    }
}
