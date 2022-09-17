package integration.authenticationservice.repository;

import annotations.RepositoryTest;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findById_shouldFindByIdOrReturnNull() {
        User user = userRepository.findById(1);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");

        User nullUser = userRepository.findById(99);
        assertThat(nullUser).isNull();
    }
}
