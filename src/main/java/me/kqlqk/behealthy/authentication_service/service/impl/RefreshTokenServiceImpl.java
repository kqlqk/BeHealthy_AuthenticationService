package me.kqlqk.behealthy.authentication_service.service.impl;

import lombok.NonNull;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.TokenNotFoundException;
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
        User user = userService.getByEmail(email);

        return refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new TokenNotFoundException("Token for user with email = " + email + " not found"));
    }

    @Override
    public boolean existsByUserEmail(@NonNull String email) {
        User user = userService.getByEmail(email);

        if (user == null) {
            return false;
        }

        return refreshTokenRepository.existsByUser(user);
    }

    @Override
    public boolean existsById(long id) {
        return refreshTokenRepository.existsById(id);
    }

    @Override
    public void save(@NonNull RefreshToken refreshToken) {
        if (existsByUserEmail(refreshToken.getUser().getEmail())) {
            throw new TokenAlreadyExistsException("Token for user with email = " + refreshToken.getUser().getEmail() + " already exists");
        }

        refreshToken.setId(0);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void update(RefreshToken refreshToken) {
        if (!existsById(refreshToken.getId())) {
            throw new TokenNotFoundException("Token with id = " + refreshToken.getId() + " not found");
        }

        refreshTokenRepository.save(refreshToken);
    }
}
