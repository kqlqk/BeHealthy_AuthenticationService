package me.kqlqk.behealthy.authenticationservice.repository;

import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findById(long id);

    RefreshToken findByToken(String token);
}
