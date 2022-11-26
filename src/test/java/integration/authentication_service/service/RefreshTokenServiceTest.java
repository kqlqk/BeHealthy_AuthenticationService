package integration.authentication_service.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
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
public class RefreshTokenServiceTest {
    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void getByUserEmail_shouldReturnRefreshTokenOrNull() {
        RefreshToken refreshToken = refreshTokenService.getByUserEmail("john@mail.com");

        assertThat(refreshToken).isNotNull();

        assertThrows(UserNotFoundException.class, () -> refreshTokenService.getByUserEmail("-"));
    }

    @Test
    public void save_shouldSaveRefreshTokenToDB() {
        int size = refreshTokenRepository.findAll().size();

        User user = userService.getById(2);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("randomToken123");
        refreshToken.setUser(user);
        refreshTokenService.save(refreshToken);

        int newSize = refreshTokenRepository.findAll().size();

        assertThat(newSize).isGreaterThan(size);


        User user2 = userService.getById(1);
        RefreshToken refreshToken2 = new RefreshToken();
        refreshToken2.setToken("randomToken1234");
        refreshToken2.setUser(user2);

        assertThrows(TokenAlreadyExistsException.class, () -> refreshTokenService.save(refreshToken2));
    }

    @Test
    public void update_shouldUpdateRefreshTokenInDB() {
        RefreshToken refreshToken = refreshTokenService.getByUserEmail("john@mail.com");
        String oldToken = refreshToken.getToken();

        refreshToken.setToken("newToken123");
        refreshTokenService.update(refreshToken);

        String newToken = refreshTokenService.getByUserEmail("john@mail.com").getToken();

        assertThat(oldToken).isNotEqualTo(newToken);
    }
}
