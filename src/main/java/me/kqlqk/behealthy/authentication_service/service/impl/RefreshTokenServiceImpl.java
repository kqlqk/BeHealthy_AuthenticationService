package me.kqlqk.behealthy.authentication_service.service.impl;

import lombok.NonNull;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenNotFoundException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.RefreshTokenRepository;
import me.kqlqk.behealthy.authentication_service.service.RefreshTokenService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }


    @Override
    public RefreshToken getByUserEmail(@NonNull String email) {
        if (!userService.existsByEmail(email)) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        User user = userService.getByEmail(email);
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user);

        if (refreshToken == null) {
            throw new TokenNotFoundException("Token for user with email = " + email + " not found");
        }

        return refreshToken;
    }

    @Override
    public boolean existsByUserEmail(@NonNull String email) {
        if (!userService.existsByEmail(email)) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        User user = userService.getByEmail(email);

        return refreshTokenRepository.existsByUser(user);
    }

    @Override
    public void save(@NonNull RefreshToken refreshToken) {
        if (refreshTokenRepository.existsById(refreshToken.getId()) || refreshTokenRepository.existsByUser(refreshToken.getUser())) {
            throw new TokenAlreadyExistsException("Token already exists");
        }

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void update(RefreshToken refreshToken) {
        if (!refreshTokenRepository.existsById(refreshToken.getId())) {
            throw new TokenNotFoundException("Token not found");
        }

        refreshTokenRepository.save(refreshToken);
    }
}
