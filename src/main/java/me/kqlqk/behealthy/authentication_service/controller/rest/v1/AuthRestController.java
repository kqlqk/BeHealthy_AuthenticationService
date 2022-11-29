package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import me.kqlqk.behealthy.authentication_service.dto.LoginDTO;
import me.kqlqk.behealthy.authentication_service.dto.RegistrationDTO;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserAlreadyExistsException;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.JWTService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    private final UserService userService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthRestController(UserService userService, JWTService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
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

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationDTO registrationDTO) {
        if (userService.existsByEmail(registrationDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email = " + registrationDTO.getEmail() + " already exists");
        }

        userService.create(registrationDTO.getName(), registrationDTO.getEmail(), registrationDTO.getPassword());

        String accessToken = jwtService.generateAccessToken(registrationDTO.getEmail());
        String refreshToken = jwtService.generateAndSaveRefreshToken(registrationDTO.getEmail());

        return ResponseEntity.ok(new TokensDTO(accessToken, refreshToken));
    }

    @PostMapping("/access")
    public Map<String, String> getNewAccessToken(@RequestBody TokensDTO tokensDTO) {
        Map<String, String> token = new HashMap<>();
        token.put("accessToken", jwtService.getNewAccessToken(tokensDTO.getRefreshToken()));

        return token;
    }

    @PostMapping("/update")
    public TokensDTO updateTokens(@RequestBody TokensDTO tokensDTO) {
        return jwtService.updateTokens(tokensDTO.getRefreshToken());
    }
}
