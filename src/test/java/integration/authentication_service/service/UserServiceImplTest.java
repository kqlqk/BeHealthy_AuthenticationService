package integration.authentication_service.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @Test
    public void create_shouldCreateUserAndAddToDb() {
        userService.create(
                "Steve",
                "steve@mail.com",
                "$2a$12$pUn8FsooabUOeZ8lGGosA.TqArkw3GEDSVoMW0ACGkzLlk7zWWYyu");

        User user = userService.getByEmail("steve@mail.com");

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Steve");
    }

    @Test
    public void update_shouldUpdateUser() {
        userService.update(
                1,
                "John",
                "newEmail@mail.com",
                "$2a$12$pUn8FsooabUOeZ8lGGosA.TqArkw3GEDSVoMW0ACGkzLlk7zWWYyu");

        User user = userService.getByEmail("newEmail@mail.com");

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");
    }

    @Test
    public void getAll_shouldReturnAllUsers() {
        assertThat(userService.getAll()).isNotEmpty();
        assertThat(userService.getAll()).hasSize(2);
    }

    @Test
    public void isValid_shouldCheckIsUserValid() {
        User validUser = userService.getById(1);
        assertThat(userService.isValid(validUser)).isTrue();

        User invalidUser = userService.getById(99);
        assertThat(userService.isValid(invalidUser)).isFalse();

        User invalidUser2 = new User(null, "steve@mail.com", "randPswd");
        assertThat(userService.isValid(invalidUser2)).isFalse();

        User invalidUser3 = new User("Steve", null, "randPswd");
        assertThat(userService.isValid(invalidUser3)).isFalse();

        User invalidUser4 = new User("Steve", "steve@mail.com", null);
        assertThat(userService.isValid(invalidUser4)).isFalse();
    }
}
