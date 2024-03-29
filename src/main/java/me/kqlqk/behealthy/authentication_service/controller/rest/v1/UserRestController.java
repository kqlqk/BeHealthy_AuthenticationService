package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import lombok.extern.slf4j.Slf4j;
import me.kqlqk.behealthy.authentication_service.dto.ValidateDTO;
import me.kqlqk.behealthy.authentication_service.dto.user_dto.CheckPasswordDTO;
import me.kqlqk.behealthy.authentication_service.dto.user_dto.GetUserDTO;
import me.kqlqk.behealthy.authentication_service.dto.user_dto.UpdateUserDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.User;
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
    public GetUserDTO getUserById(@PathVariable long id) {
        return GetUserDTO.convert(userService.getById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        userService.update(new User(id, updateUserDTO.getName(), updateUserDTO.getEmail(), updateUserDTO.getPassword()));

        log.info("User with id = " + id + " was updated");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public GetUserDTO getUserByEmail(@RequestParam String email) {
        return GetUserDTO.convert(userService.getByEmail(email));
    }

    @PostMapping("/users/{id}/password/check")
    public ValidateDTO checkUserPassword(@PathVariable long id, @RequestBody @Valid CheckPasswordDTO checkPasswordDTO) {
        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        ValidateDTO validateDTO = new ValidateDTO();

        validateDTO.setValid(passwordEncoder.matches(checkPasswordDTO.getPassword(), userService.getById(id).getPassword()));

        return validateDTO;
    }
}
