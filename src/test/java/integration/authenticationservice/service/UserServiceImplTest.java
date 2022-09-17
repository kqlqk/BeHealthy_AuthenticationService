package integration.authenticationservice.service;

import annotations.ServiceTest;
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
}
