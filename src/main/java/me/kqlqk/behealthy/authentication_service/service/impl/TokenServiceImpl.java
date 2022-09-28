package me.kqlqk.behealthy.authentication_service.service.impl;

import io.jsonwebtoken.*;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.RefreshTokenRepository;
import me.kqlqk.behealthy.authentication_service.repository.UserRepository;
import me.kqlqk.behealthy.authentication_service.service.TokenService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
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
    private final UserRepository userRepository;


    @Autowired
    public TokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshToken getRefreshTokenById(long id) {
        return refreshTokenRepository.findById(id);
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
    public RefreshToken createRefreshToken(String email) {
        if (!userService.isValid(userService.getByEmail(email))) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        User user = userService.getByEmail(email);

        RefreshToken refreshToken = new RefreshToken();

        Claims claims = Jwts.claims().setSubject(user.getEmail());

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
        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return getRefreshTokenByStringToken(token);
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

            if (!userService.getByEmail(email).getRefreshToken().getToken().equals(token)) {
                return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

        return claims.getBody().getExpiration().after(new Date());
    }

    @Override
    public RefreshToken updateRefreshToken(String email) {
        if (!userService.isValid(userService.getByEmail(email))) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        User user = userService.getByEmail(email);

        if (user.getRefreshToken() == null) {
            throw new TokenException("User with email = " + user.getEmail() + " hasn't a refresh token");
        }

        RefreshToken refreshToken = user.getRefreshToken();

        Claims claims = Jwts.claims().setSubject(user.getEmail());

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
        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return refreshToken;
    }

    @Override
    public Map<String, String> updateAccessAndRefreshToken(String email) {
        if (!userService.isValid(userService.getByEmail(email))) {
            throw new UserNotFoundException("User not found");
        }

        User user = userService.getByEmail(email);

        Map<String, String> tokens = new HashMap<>();
        String accessToken = createAccessToken(user.getEmail());
        RefreshToken refreshToken = updateRefreshToken(user.getEmail());

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

