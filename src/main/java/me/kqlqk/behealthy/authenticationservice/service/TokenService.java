package me.kqlqk.behealthy.authenticationservice.service;

import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.model.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public interface TokenService {
    RefreshToken getRefreshTokenByStringToken(String token);

    String createAccessToken(String email);

    RefreshToken createAndSaveRefreshToken(String email);

    Map<String, String> updateAccessAndRefreshToken(User user);

    boolean isAccessTokenValid(String token);

    boolean isRefreshTokenValid(String token);

    String getEmailByAccessToken(String token);

    String getEmailByRefreshToken(String token);

    String getAccessTokenFromHeader(HttpServletRequest request);

    String getRefreshTokenFromHeader(HttpServletRequest request);
}
