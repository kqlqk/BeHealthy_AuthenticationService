package integration.authentication_service.repository;

import annotations.RepositoryTest;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.RefreshTokenRepository;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class RefreshTokenRepositoryTest {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Test
    public void findByUser_shouldFindByUserOrReturnNull() {
        User user = userService.getById(1);

        assertThat(refreshTokenRepository.findByUser(user)).isNotNull();

        assertThat(refreshTokenRepository.findByUser(null)).isNull();
    }
}
