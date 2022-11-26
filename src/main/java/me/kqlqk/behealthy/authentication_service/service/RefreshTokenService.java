package me.kqlqk.behealthy.authentication_service.service;

import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    RefreshToken getByUserEmail(String email);

    boolean existsByUser(User user);

    void save(RefreshToken refreshToken);

    void update(RefreshToken refreshToken);
}
