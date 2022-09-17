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

    @Test
    public void findByEmail_shouldFindByEmailOrReturnNull() {
        User user = userRepository.findByEmail("john@mail.com");

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");

        User nullUser = userRepository.findByEmail("random@mail.com");
        assertThat(nullUser).isNull();
    }

    @Test
    public void existsByEmail_shouldCheckIfExistsByEmail() {
        assertThat(userRepository.existsByEmail("john@mail.com")).isTrue();
        assertThat(userRepository.existsByEmail("random@mail.com")).isFalse();
    }

    @Test
    public void existsById_shouldCheckIfExistsById() {
        assertThat(userRepository.existsById(1)).isTrue();
        assertThat(userRepository.existsById(99)).isFalse();
    }
}
