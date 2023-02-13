package me.kqlqk.behealthy.authentication_service.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.JWTService;
import me.kqlqk.behealthy.authentication_service.service.RefreshTokenService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService {
    private final SecretKey accessTokenSecret;
    private final SecretKey refreshTokenSecret;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public JWTServiceImpl(@Value("${jwt.access.secret}") String accessTokenSecret,
                          @Value("${jwt.refresh.secret}") String refreshTokenSecret,
                          @Autowired UserService userService,
                          @Autowired RefreshTokenService refreshTokenService) {
        this.accessTokenSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
        this.refreshTokenSecret = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(refreshTokenSecret));
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public String generateAccessToken(@NonNull String userEmail) {
        if (!userService.existsByEmail(userEmail)) {
            throw new UserNotFoundException("User with email = " + userEmail + " not found");
        }

        LocalDateTime now = LocalDateTime.now();
        Date expiresIn = Date.from(now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(userEmail)
                .setExpiration(expiresIn)
                .signWith(accessTokenSecret)
                .compact();
    }

    @Override
    public String generateAndSaveOrUpdateRefreshToken(@NonNull String userEmail) {
        if (!userService.existsByEmail(userEmail)) {
            throw new UserNotFoundException("User with email = " + userEmail + " not found");
        }

        LocalDateTime now = LocalDateTime.now();
        Date expiresIn = Date.from(now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        String token = Jwts.builder()
                .setSubject(userEmail)
                .setExpiration(expiresIn)
                .signWith(refreshTokenSecret)
                .compact();

        if (refreshTokenService.existsByUserEmail(userEmail)) {
            RefreshToken refreshToken = refreshTokenService.getByUserEmail(userEmail);
            refreshToken.setToken(token);
            refreshTokenService.update(refreshToken);
        }
        else {
            User user = userService.getByEmail(userEmail);

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(token);
            refreshTokenService.save(refreshToken);
        }

        return token;
    }

    @Override
    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, accessTokenSecret);
    }

    @Override
    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, refreshTokenSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (ExpiredJwtException expEx) {
            throw new TokenException("Token expired");
        }
        catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        }
        catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        }
        catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        }
        catch (Exception e) {
            throw new TokenException("Token invalid");
        }

    }

    @Override
    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, accessTokenSecret);
    }

    @Override
    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, refreshTokenSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (ExpiredJwtException expEx) {
            throw new TokenException("Token expired");
        }
        catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        }
        catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        }
        catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        }
        catch (Exception e) {
            throw new TokenException("Token invalid");
        }
    }

    @Override
    public String getNewAccessToken(@NonNull String refreshToken) {
        if (!validateRefreshToken(refreshToken)) {
            throw new TokenException("Token invalid");
        }

        String email = getRefreshClaims(refreshToken).getSubject();
        String refreshTokenDB = refreshTokenService.getByUserEmail(email).getToken();

        if (refreshTokenDB.equals(refreshToken)) {
            return generateAccessToken(email);
        }
        else {
            throw new TokenException("Token invalid");
        }
    }

    @Override
    public TokensDTO updateTokens(@NonNull String refreshToken) {
        if (!validateRefreshToken(refreshToken)) {
            throw new TokenException("Token invalid");
        }

        String email = getRefreshClaims(refreshToken).getSubject();
        String refreshTokenDB = refreshTokenService.getByUserEmail(email).getToken();

        if (refreshTokenDB.equals(refreshToken)) {
            String newAccessToken = generateAccessToken(email);
            String newRefreshToken = generateAndSaveOrUpdateRefreshToken(email);

            RefreshToken refreshTokenToUpdate = refreshTokenService.getByUserEmail(email);
            refreshTokenToUpdate.setToken(newRefreshToken);
            refreshTokenService.update(refreshTokenToUpdate);

            return new TokensDTO(newAccessToken, newRefreshToken);
        }
        else {
            throw new TokenException("Token invalid");
        }
    }
}
