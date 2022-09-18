package me.kqlqk.behealthy.authenticationservice.service.impl;

import me.kqlqk.behealthy.authenticationservice.exceptions.UserAlreadyExistsException;
import me.kqlqk.behealthy.authenticationservice.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.repository.UserRepository;
import me.kqlqk.behealthy.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void create(String name, String email, String password, byte age) {
        if (existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email = " + email + " already exists");
        }
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (email == null || email.equals("")) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Name cannot be less than 1");
        }

        User user = new User(name, email, password, age, null);
        userRepository.save(user);
    }

    @Override
    public void update(long id, String name, String email, String password, byte age) {
        if (!existsById(id)) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (email == null || email.equals("")) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Name cannot be less than 1");
        }

        User user = getById(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAge(age);

        userRepository.save(user);
    }

    @Override
    public boolean isValid(User user) {
        return user != null &&
                existsById(user.getId()) &&
                (user.getName() != null || !user.getName().equals("")) &&
                (user.getEmail() != null || !user.getEmail().equals("")) &&
                (user.getPassword() != null || !user.getPassword().equals("")) &&
                user.getAge() > 0;
    }
}
