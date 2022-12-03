package me.kqlqk.behealthy.authentication_service.service;

import io.jsonwebtoken.Claims;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import org.springframework.stereotype.Service;

@Service
public interface JWTService {
    String generateAccessToken(String userEmail);

    String generateAndSaveRefreshToken(String userEmail);

    boolean validateAccessToken(String accessToken);

    boolean validateRefreshToken(String refreshToken);

    Claims getAccessClaims(String accessToken);

    Claims getRefreshClaims(String refreshToken);

    String getNewAccessToken(String refreshToken);

    TokensDTO updateTokens(String refreshToken);
}
