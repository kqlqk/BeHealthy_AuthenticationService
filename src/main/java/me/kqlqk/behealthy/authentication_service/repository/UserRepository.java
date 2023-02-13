package me.kqlqk.behealthy.authentication_service.repository;

import me.kqlqk.behealthy.authentication_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
