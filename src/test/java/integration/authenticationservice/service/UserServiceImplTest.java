package integration.authenticationservice.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.service.impl.UserServiceImpl;
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
                "$2a$12$pUn8FsooabUOeZ8lGGosA.TqArkw3GEDSVoMW0ACGkzLlk7zWWYyu",
                (byte) 22);

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
                "$2a$12$pUn8FsooabUOeZ8lGGosA.TqArkw3GEDSVoMW0ACGkzLlk7zWWYyu",
                (byte) 20);

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

        User invalidUser2 = new User(null, "steve@mail.com", "randPswd", (byte) 56, new RefreshToken());
        assertThat(userService.isValid(invalidUser2)).isFalse();

        User invalidUser3 = new User("Steve", null, "randPswd", (byte) 56, new RefreshToken());
        assertThat(userService.isValid(invalidUser3)).isFalse();

        User invalidUser4 = new User("Steve", "steve@mail.com", null, (byte) 56, new RefreshToken());
        assertThat(userService.isValid(invalidUser4)).isFalse();

        User invalidUser5 = new User("Steve", "steve@mail.com", "randPswd", (byte) 0, new RefreshToken());
        assertThat(userService.isValid(invalidUser5)).isFalse();

        User invalidUser6 = new User("Steve", "steve@mail.com", "randPswd", (byte) 0, null);
        assertThat(userService.isValid(invalidUser6)).isFalse();
    }
}
