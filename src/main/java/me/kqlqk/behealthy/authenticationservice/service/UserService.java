package me.kqlqk.behealthy.authenticationservice.service;

import me.kqlqk.behealthy.authenticationservice.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User getById(long id);

    User getByEmail(String email);

    List<User> getAll();

    boolean existsById(long id);
    boolean existsByEmail(String email);
    void create(String name, String email, String password, byte age);
    void update(long id, String name, String email, String password, byte age);

    boolean isValid(User user);
}
