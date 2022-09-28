package me.kqlqk.behealthy.authentication_service.service;

import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public interface TokenService {
    RefreshToken getRefreshTokenById(long id);

    RefreshToken getRefreshTokenByStringToken(String token);

    String createAccessToken(String email);

    RefreshToken createRefreshToken(String email);

    RefreshToken updateRefreshToken(String email);

    Map<String, String> updateAccessAndRefreshToken(String email);

    boolean isAccessTokenValid(String token);

    boolean isRefreshTokenValid(String token);

    String getEmailByAccessToken(String token);

    String getEmailByRefreshToken(String token);

    String getAccessTokenFromHeader(HttpServletRequest request);

    String getRefreshTokenFromHeader(HttpServletRequest request);
}
