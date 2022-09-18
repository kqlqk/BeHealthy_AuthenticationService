package integration.authenticationservice.service;

import annotations.ServiceTest;
import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.service.UserService;
import me.kqlqk.behealthy.authenticationservice.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ServiceTest
public class TokenServiceImplTest {
    @Autowired
    private TokenServiceImpl tokenService;

    @Autowired
    private UserService userService;

    @Test
    public void createAccessToken_shouldCreateAccessToken() {
        String accessToken = tokenService.createAccessToken("john@mail.com");

        assertThat(accessToken).isNotNull();
    }

    @Test
    public void createAndSaveRefreshToken_shouldCreateRefreshTokenAndSaveItInDb() {
        RefreshToken refreshToken = tokenService.createAndSaveRefreshToken("john@mail.com");

        assertThat(refreshToken).isNotNull();
    }

    @Test
    public void isAccessTokenValid_shouldCheckIsAccessTokenValid() {
        String accessToken = tokenService.createAccessToken("john@mail.com");

        assertThat(tokenService.isAccessTokenValid(accessToken)).isTrue();

        assertThat(tokenService.isAccessTokenValid(null)).isFalse();

    }

    @Test
    public void isRefreshTokenValid_shouldCheckIsRefreshTokenValid() {
        RefreshToken refreshToken = tokenService.createAndSaveRefreshToken("john@mail.com");

        assertThat(tokenService.isRefreshTokenValid(refreshToken.getToken())).isTrue();

        assertThat(tokenService.isRefreshTokenValid(null)).isFalse();
    }

    @Test
    public void updateAccessAndRefreshToken_shouldUpdateAccessAndRefreshToken() {
        User user = userService.getById(1);

        Map<String, String> tokens = tokenService.updateAccessAndRefreshToken(user);

        assertThat(tokens.get("access")).isNotNull();
        assertThat(tokens.get("refresh")).isNotNull();
    }

    @Test
    public void getEmailByAccessToken_shouldGetEmailByAccessToken() {
        String accessToken = tokenService.createAccessToken("john@mail.com");

        assertThat(tokenService.getEmailByAccessToken(accessToken)).isEqualTo("john@mail.com");
    }

    @Test
    public void getEmailByRefreshToken_shouldGetEmailByRefreshToken() {
        RefreshToken refreshToken = tokenService.createAndSaveRefreshToken("john@mail.com");

        assertThat(tokenService.getEmailByRefreshToken(refreshToken.getToken())).isEqualTo("john@mail.com");
    }

    @Test
    public void getAccessTokenFromHeader_shouldGetAccessTokenFromAuthorizationHeader() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("Bearer_token").when(request).getHeader("Authorization_access");

        assertThat(tokenService.getAccessTokenFromHeader(request)).isEqualTo("token");
    }

    @Test
    public void getRefreshTokenFromHeader_shouldGetRefreshTokenFromAuthorizationHeader() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("Bearer_token").when(request).getHeader("Authorization_refresh");

        assertThat(tokenService.getRefreshTokenFromHeader(request)).isEqualTo("token");
    }
}

