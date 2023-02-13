package me.kqlqk.behealthy.authentication_service.service;

import me.kqlqk.behealthy.authentication_service.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User getById(long id);

    User getByEmail(String email);

    List<User> getAll();

    boolean existsById(long id);

    boolean existsByEmail(String email);

    void create(User user);

    void update(User user);
}
