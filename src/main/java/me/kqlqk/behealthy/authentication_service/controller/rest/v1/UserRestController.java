package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import me.kqlqk.behealthy.authentication_service.dto.LoginDTO;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.JWTService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserRestController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Autowired
    public UserRestController(UserService userService, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UserDTO userDTO) {
        userService.update(id, userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());
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
    public ResponseEntity<?> checkUserPassword(@PathVariable long id, @RequestBody String oldPassword) {
        if (!userService.existsById(id)) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        Map<String, Boolean> validation = new HashMap<>();

        if (passwordEncoder.matches(oldPassword, getUserById(id).getPassword())) {
            validation.put("valid", true);
        } else {
            validation.put("valid", false);
        }

        return ResponseEntity.ok(validation);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        if (!userService.existsByEmail(loginDTO.getEmail())) {
            throw new UserNotFoundException("Bad credentials");
        }

        User user = userService.getByEmail(loginDTO.getEmail());

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Bad credentials");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateAndSaveRefreshToken(user.getEmail());

        return ResponseEntity.ok(new TokensDTO(accessToken, refreshToken));
    }

    @PostMapping("/auth/access")
    public Map<String, String> getNewAccessToken(@RequestBody TokensDTO tokensDTO) {
        Map<String, String> token = new HashMap<>();
        token.put("refreshToken", jwtService.getNewAccessToken(tokensDTO.getRefreshToken()));

        return token;
    }

    @PostMapping("/auth/update")
    public TokensDTO updateTokens(@RequestBody TokensDTO tokensDTO) {
        return jwtService.updateTokens(tokensDTO.getRefreshToken());
    }
}
