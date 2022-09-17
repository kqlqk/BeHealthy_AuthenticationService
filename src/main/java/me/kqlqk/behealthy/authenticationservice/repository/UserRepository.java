package me.kqlqk.behealthy.authenticationservice.repository;

import me.kqlqk.behealthy.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);

}
