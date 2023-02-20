package integration.authentication_service.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.RefreshTokenRepository;
import me.kqlqk.behealthy.authentication_service.service.RefreshTokenService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import me.kqlqk.behealthy.authentication_service.service.impl.JWTServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class JWTServiceImplTest {
    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    public void generateAccessToken_shouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> jwtService.generateAccessToken("random"));
    }

    @Test
    public void generateAndSaveOrUpdateRefreshToken_shouldSaveRefreshTokenToDb() {
        User user = new User("name", "email@mail.com", "Pswd1234");
        userService.save(user);

        int size = refreshTokenRepository.findAll().size();

        jwtService.generateAndSaveOrUpdateRefreshToken(user.getEmail());

        int newSize = refreshTokenRepository.findAll().size();

        assertThat(newSize).isEqualTo(size + 1);
    }

    @Test
    public void generateAndSaveOrUpdateRefreshToken_shouldUpdateRefreshTokenInDb() {
        RefreshToken refreshToken = refreshTokenService.getByUserEmail("user1@mail.com");

        String newToken = jwtService.generateAndSaveOrUpdateRefreshToken("user1@mail.com");

        assertThat(refreshToken.getToken()).isNotEqualTo(newToken);
    }
}
