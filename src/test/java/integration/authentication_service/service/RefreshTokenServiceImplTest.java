package integration.authentication_service.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.RefreshTokenRepository;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import me.kqlqk.behealthy.authentication_service.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ServiceTest
public class RefreshTokenServiceImplTest {
    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Test
    public void save_shouldSaveRefreshTokenToDB() {
        int size = refreshTokenRepository.findAll().size();

        User user = userService.getById(2);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("randomToken123");
        refreshToken.setUser(user);
        refreshTokenService.save(refreshToken);

        int newSize = refreshTokenRepository.findAll().size();

        assertThat(newSize).isEqualTo(size + 1);
    }

    @Test
    public void save_shouldThrowException() {
        User user = userService.getById(1);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("randomToken1234");
        refreshToken.setUser(user);

        assertThrows(TokenAlreadyExistsException.class, () -> refreshTokenService.save(refreshToken));
    }

    @Test
    public void update_shouldUpdateRefreshTokenInDB() {
        RefreshToken refreshToken = refreshTokenService.getByUserEmail("user1@mail.com");
        refreshToken.setToken("newToken123");

        refreshTokenService.update(refreshToken);

        assertThat(refreshTokenService.getByUserEmail("user1@mail.com").getToken()).isEqualTo(refreshToken.getToken());
    }

    @Test
    public void update_shouldThrowException() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(99);

        assertThrows(TokenNotFoundException.class, () -> refreshTokenService.update(refreshToken));
    }
}
