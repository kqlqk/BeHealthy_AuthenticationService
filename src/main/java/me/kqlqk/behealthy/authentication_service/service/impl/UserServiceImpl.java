package me.kqlqk.behealthy.authentication_service.service.impl;

import lombok.NonNull;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.repository.UserRepository;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
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
    public void create(@NonNull UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            throw new NullPointerException("userDTO is marked non-null but is null");
        }

        userDTO.setEmail(userDTO.getEmail().toLowerCase());

        if (existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email = " + userDTO.getEmail() + " already exists");
        }

        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(userDTO);

        if (!constraintViolations.isEmpty()) {
            throw new UserException(constraintViolations.iterator().next().getMessage());
        }

        User user = new User(userDTO.getName(), userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void update(@NonNull UserDTO userDTO) {
        if (!existsById(userDTO.getId())) {
            throw new UserNotFoundException("User with id = " + userDTO.getId() + " not found");
        }

        User user = getById(userDTO.getId());

        if (userDTO.getName() != null) {
            Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validateProperty(userDTO, "name");

            if (constraintViolations.isEmpty()) {
                user.setName(userDTO.getName());
            } else {
                throw new UserException(constraintViolations.iterator().next().getMessage());
            }
        }

        if (userDTO.getEmail() != null) {
            Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validateProperty(userDTO, "email");

            if (constraintViolations.isEmpty()) {
                user.setEmail(userDTO.getEmail().toLowerCase());
            } else {
                throw new UserException(constraintViolations.iterator().next().getMessage());
            }
        }

        if (userDTO.getPassword() != null) {
            Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validateProperty(userDTO, "password");

            if (constraintViolations.isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            } else {
                throw new UserException(constraintViolations.iterator().next().getMessage());
            }
        }

        if (userDTO.getName() == null && userDTO.getEmail() == null && userDTO.getPassword() == null) {
            throw new IllegalArgumentException("Minimum 1 field should be updated");
        }

        userRepository.save(user);
    }

}
