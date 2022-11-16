package me.kqlqk.behealthy.authentication_service.service.impl;

import lombok.NonNull;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.UserRepository;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void create(@NonNull String name, @NonNull String email, @NonNull String password) {
        if (existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email = " + email + " already exists");
        }

        User user = new User(name, email, passwordEncoder.encode(password), null);
        userRepository.save(user);
    }

    @Override
    public void update(long id, String name, String email, String password) {
        if (!existsById(id)) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        User user = getById(id);

        if (name != null && !name.equals("")) {
            user.setName(name);
        }

        if (email != null && !email.equals("")) {
            if (!email.matches("^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$")) {
                throw new IllegalArgumentException("Email should be valid");
            }
            if (existsByEmail(email) && !email.equals(user.getEmail())) {
                throw new IllegalArgumentException("User with email = " + email + " already exists");
            }

            user.setEmail(email);
        }

        if (password != null && !password.equals("")) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if ((name == null || name.equals("")) &&
                (email == null || email.equals("")) &&
                (password == null || password.equals(""))) {
            throw new IllegalArgumentException("Minimum 1 field should be updated");
        }

        userRepository.save(user);
    }

    @Override
    public boolean isValid(User user) {
        return user != null &&
                existsById(user.getId()) &&
                (user.getName() != null || !user.getName().equals("")) &&
                (user.getEmail() != null || !user.getEmail().equals("")) &&
                (user.getPassword() != null || !user.getPassword().equals(""));
    }
}
