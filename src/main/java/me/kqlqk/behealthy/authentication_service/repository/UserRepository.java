package me.kqlqk.behealthy.authentication_service.repository;

import me.kqlqk.behealthy.authentication_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
