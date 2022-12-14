package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import lombok.extern.slf4j.Slf4j;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.dto.ValidateDTO;
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
@Slf4j
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
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        userDTO.setEmail(userDTO.getEmail().toLowerCase());

        if (!userService.existsByEmail(userDTO.getEmail())) {
            throw new UserNotFoundException("Bad credentials");
        }

        User user = userService.getByEmail(userDTO.getEmail());

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Bad credentials");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateAndSaveRefreshToken(user.getEmail());

        log.info("User with id = " + user.getId() + " got new accessToken, refreshToken");

        return ResponseEntity.ok(new TokensDTO(accessToken, refreshToken));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid UserDTO userDTO) {
        userService.create(userDTO);

        String accessToken = jwtService.generateAccessToken(userDTO.getEmail());
        String refreshToken = jwtService.generateAndSaveRefreshToken(userDTO.getEmail());

        long userId = userService.getByEmail(userDTO.getEmail()).getId();
        log.info("User with id = " + userId + " registered, got new accessToken, refreshToken");

        return ResponseEntity.ok(new TokensDTO(accessToken, refreshToken));
    }

    @PostMapping("/access")
    public Map<String, String> getNewAccessToken(@RequestBody TokensDTO tokensDTO) {
        Map<String, String> token = new HashMap<>();
        token.put("accessToken", jwtService.getNewAccessToken(tokensDTO.getRefreshToken()));

        String userEmail = jwtService.getRefreshClaims(tokensDTO.getRefreshToken()).getSubject();
        long userId = userService.getByEmail(userEmail).getId();
        log.info("User with id = " + userId + " got new accessToken");

        return token;
    }

    @PostMapping("/update")
    public TokensDTO updateTokens(@RequestBody TokensDTO tokensDTO) {
        TokensDTO tokens = jwtService.updateTokens(tokensDTO.getRefreshToken());

        String userEmail = jwtService.getRefreshClaims(tokensDTO.getRefreshToken()).getSubject();
        long userId = userService.getByEmail(userEmail).getId();
        log.info("User with id = " + userId + " updated tokens");

        return tokens;
    }

    @PostMapping("/access/validate")
    public ValidateDTO validateAccessToken(@RequestBody TokensDTO tokensDTO) {
        ValidateDTO validateDTO = new ValidateDTO();
        validateDTO.setValid(jwtService.validateAccessToken(tokensDTO.getAccessToken()));

        return validateDTO;
    }

    @PostMapping("/access/email")
    public Map<String, String> getEmailFromAccessToken(@RequestBody TokensDTO tokensDTO) {
        Map<String, String> res = new HashMap<>();
        res.put("email", jwtService.getAccessClaims(tokensDTO.getAccessToken()).getSubject());

        return res;
    }
}
