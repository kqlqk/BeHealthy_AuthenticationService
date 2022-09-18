package me.kqlqk.behealthy.authenticationservice.service.impl;

import io.jsonwebtoken.*;
import me.kqlqk.behealthy.authenticationservice.exceptions.TokenException;
import me.kqlqk.behealthy.authenticationservice.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.repository.RefreshTokenRepository;
import me.kqlqk.behealthy.authenticationservice.service.TokenService;
import me.kqlqk.behealthy.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {
    @Value("${jwt.access.expired}")
    private long accessTokenValidityMilliseconds;

    @Value("${jwt.refresh.expired}")
    private long refreshTokenValidityMilliseconds;

    @Value("${jwt.access.secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshTokenSecret;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;


    @Autowired
    public TokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Override
    public RefreshToken getRefreshTokenByStringToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public String createAccessToken(String email) {
        if (!userService.isValid(userService.getByEmail(email))) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        Claims claims = Jwts.claims().setSubject(email);

        Date current = new Date();
        Date validity = new Date(current.getTime() + accessTokenValidityMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(current)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, accessTokenSecret)
                .compact();
    }

    @Override
    public RefreshToken createAndSaveRefreshToken(String email) {
        if (!userService.isValid(userService.getByEmail(email))) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        RefreshToken refreshToken = new RefreshToken();

        Claims claims = Jwts.claims().setSubject(email);

        Date current = new Date();
        Date validity = new Date(current.getTime() + refreshTokenValidityMilliseconds);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(current)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, refreshTokenSecret)
                .compact();

        refreshToken.setToken(token);
        refreshToken.setExpires(validity.getTime());

        return refreshToken;
    }

    @Override
    public boolean isAccessTokenValid(String token) {
        if (token == null || token.equals("")) {
            return false;
        }

        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(accessTokenSecret).build().parseClaimsJws(token);
            String email = getEmailByAccessToken(token);

            if (!userService.isValid(userService.getByEmail(email))) {
                return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

        return claims.getBody().getExpiration().after(new Date());
    }

    @Override
    public boolean isRefreshTokenValid(String token) {
        if (token == null || token.equals("")) {
            return false;
        }

        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(refreshTokenSecret).build().parseClaimsJws(token);
            String email = getEmailByRefreshToken(token);

            if (!userService.isValid(userService.getByEmail(email))) {
                return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

        return claims.getBody().getExpiration().after(new Date());
    }

    @Override
    public Map<String, String> updateAccessAndRefreshToken(User user) {
        Map<String, String> tokens = new HashMap<>();

        RefreshToken refreshToken = createAndSaveRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);

        String accessToken = createAccessToken(user.getEmail());

        tokens.put("access", accessToken);
        tokens.put("refresh", refreshToken.getToken());

        return tokens;
    }

    @Override
    public String getEmailByAccessToken(String token) {
        if (token == null || token.equals("")) {
            throw new IllegalArgumentException("Access token cannot be null");
        }

        try {
            return Jwts.parserBuilder().setSigningKey(accessTokenSecret).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            throw new TokenException("Access token cannot be parsed");
        }
    }

    @Override
    public String getEmailByRefreshToken(String token) {
        if (token == null || token.equals("")) {
            throw new IllegalArgumentException("Refresh token cannot be null");
        }

        try {
            return Jwts.parserBuilder().setSigningKey(refreshTokenSecret).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            throw new TokenException("Refresh token cannot be parsed");
        }
    }

    @Override
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest cannot be null");
        }

        String bearerWithToken = request.getHeader("Authorization_access");

        if (bearerWithToken == null) {
            throw new TokenException("Authorization_access header not found");
        }
        if (!bearerWithToken.startsWith("Bearer_")) {
            throw new TokenException("Access token should starts with Bearer_");
        }

        return bearerWithToken.substring(7);
    }

    @Override
    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest cannot be null");
        }

        String bearerWithToken = request.getHeader("Authorization_refresh");

        if (bearerWithToken == null) {
            throw new TokenException("Authorization_refresh header not found");
        }
        if (!bearerWithToken.startsWith("Bearer_")) {
            throw new TokenException("Refresh token should starts with Bearer_");
        }

        return bearerWithToken.substring(7);
    }
}

