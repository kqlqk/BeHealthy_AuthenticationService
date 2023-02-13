package unit.authentication_service.service;

import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.RefreshTokenService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import me.kqlqk.behealthy.authentication_service.service.impl.JWTServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTServiceImpTest {
    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private JWTServiceImpl jwtService;

    @BeforeEach
    public void init() {
        jwtService = new JWTServiceImpl(
                "randomAccessSecretTest123456789000000000000",
                "randomRefreshSecretTest123456789000000000000",
                userService,
                refreshTokenService);
    }


    @Test
    public void generateAccessToken_shouldGenerateAccessToken() {
        String userEmail = "random@mail.com";
        when(userService.existsByEmail(userEmail)).thenReturn(true);

        String accessToken = jwtService.generateAccessToken(userEmail);

        assertThat(accessToken).matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");
    }

    @Test
    public void generateAndSaveRefreshToken_shouldGenerateAndUpdateRefreshToken() {
        String userEmail = "random@mail.com";
        RefreshToken rt = new RefreshToken();

        when(userService.existsByEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.existsByUserEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.getByUserEmail(userEmail)).thenReturn(rt);

        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(userEmail);

        assertThat(refreshToken).matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");
    }

    @Test
    public void generateAndSaveRefreshToken_shouldGenerateAndSaveRefreshToken() {
        String userEmail = "random@mail.com";
        User u = new User();

        when(userService.existsByEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.existsByUserEmail(userEmail)).thenReturn(false);
        when(userService.getByEmail(userEmail)).thenReturn(u);

        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(userEmail);

        assertThat(refreshToken).matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");
    }

    @Test
    public void validateAccessToken_shouldValidateAccessToken() {
        String userEmail = "random@mail.com";
        when(userService.existsByEmail(userEmail)).thenReturn(true);
        String accessToken = jwtService.generateAccessToken(userEmail);

        assertThat(jwtService.validateAccessToken(accessToken)).isTrue();
    }

    @Test
    public void validateAccessToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtService.validateAccessToken(""));
    }

    @Test
    public void validateRefreshToken_shouldValidateRefreshToken() {
        String userEmail = "random@mail.com";
        User u = new User();
        when(userService.existsByEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.existsByUserEmail(userEmail)).thenReturn(false);
        when(userService.getByEmail(userEmail)).thenReturn(u);
        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(userEmail);

        assertThat(jwtService.validateRefreshToken(refreshToken)).isTrue();
    }

    @Test
    public void validateRefreshToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtService.validateRefreshToken(""));
    }

    @Test
    public void getAccessClaims_shouldReturnAccessClaims() {
        String userEmail = "random@mail.com";
        when(userService.existsByEmail(userEmail)).thenReturn(true);
        String accessToken = jwtService.generateAccessToken(userEmail);

        assertThat(jwtService.getAccessClaims(accessToken)).isNotNull();
    }

    @Test
    public void getAccessClaims_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtService.getAccessClaims(""));
    }

    @Test
    public void getRefreshClaims_shouldReturnRefreshClaims() {
        String userEmail = "random@mail.com";
        User u = new User();

        when(userService.existsByEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.existsByUserEmail(userEmail)).thenReturn(false);
        when(userService.getByEmail(userEmail)).thenReturn(u);

        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(userEmail);

        assertThat(jwtService.getRefreshClaims(refreshToken)).isNotNull();
    }

    @Test
    public void getRefreshClaims_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtService.getRefreshClaims(""));
    }

    @Test
    public void getNewAccessToken_shouldReturnNewAccessToken() {
        String userEmail = "random@mail.com";
        User u = new User();
        RefreshToken rt = new RefreshToken();

        when(userService.existsByEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.existsByUserEmail(userEmail)).thenReturn(false);
        when(userService.getByEmail(userEmail)).thenReturn(u);

        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(userEmail);

        rt.setToken(refreshToken);
        when(refreshTokenService.getByUserEmail(userEmail)).thenReturn(rt);

        assertThat(jwtService.getNewAccessToken(refreshToken)).matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");
    }

    @Test
    public void getNewAccessToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtService.getNewAccessToken("-"));
    }

    @Test
    public void updateTokens_shouldReturnNewAccessAndRefreshTokensOrThrowException() {
        String userEmail = "random@mail.com";
        User u = new User();
        RefreshToken rt = new RefreshToken();

        when(userService.existsByEmail(userEmail)).thenReturn(true);
        when(refreshTokenService.existsByUserEmail(userEmail)).thenReturn(false);
        when(userService.getByEmail(userEmail)).thenReturn(u);

        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(userEmail);

        rt.setToken(refreshToken);
        when(refreshTokenService.getByUserEmail(userEmail)).thenReturn(rt);

        assertThat(jwtService.updateTokens(refreshToken).getAccessToken()).matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");
        assertThat(jwtService.updateTokens(refreshToken).getRefreshToken()).matches("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");
    }

    @Test
    public void updateTokens_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtService.updateTokens("-"));
    }
}
