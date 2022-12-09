package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import lombok.extern.slf4j.Slf4j;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.dto.ValidateDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UserRestController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable long id) {
        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        return UserDTO.convertFromUserToUserDTO(userService.getById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO) {
        userService.create(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());

        log.info(userDTO.getEmail() + " was created");

        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UserDTO userDTO) {
        userService.update(id, userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());

        log.info(userDTO.getEmail() + " was updated");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersOrSpecified(@RequestParam(required = false) String email) {
        if (email == null || email.equals("")) {
            return ResponseEntity.ok(UserDTO.convertListOfUsersToListOfUserDTOs(userService.getAll()));
        }

        if (!userService.existsByEmail(email)) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        return ResponseEntity.ok(UserDTO.convertFromUserToUserDTO(userService.getByEmail(email)));
    }

    @PostMapping("/users/{id}/password/check")
    public ValidateDTO checkUserPassword(@PathVariable long id, @RequestBody String password) {
        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        ValidateDTO validateDTO = new ValidateDTO();

        validateDTO.setValid(passwordEncoder.matches(password, getUserById(id).getPassword()));

        return validateDTO;
    }
}
