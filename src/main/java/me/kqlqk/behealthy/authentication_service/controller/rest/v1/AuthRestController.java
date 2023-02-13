package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import lombok.extern.slf4j.Slf4j;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.dto.ValidateDTO;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.LoginDTO;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.RegistrationDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.JWTService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public TokensDTO login(@RequestBody @Valid LoginDTO loginDTO) {
        loginDTO.setEmail(loginDTO.getEmail().toLowerCase());

        if (!userService.existsByEmail(loginDTO.getEmail())) {
            throw new UserNotFoundException("Bad credentials");
        }

        User user = userService.getByEmail(loginDTO.getEmail());

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Bad credentials");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(user.getEmail());

        log.info("User with id = " + user.getId() + " got new accessToken, refreshToken");

        return new TokensDTO(user.getId(), accessToken, refreshToken);
    }

    @PostMapping("/registration")
    public TokensDTO registration(@RequestBody @Valid RegistrationDTO registrationDTO) {
        userService.create(new User(registrationDTO.getName(), registrationDTO.getEmail(), registrationDTO.getPassword()));

        String accessToken = jwtService.generateAccessToken(registrationDTO.getEmail());
        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken(registrationDTO.getEmail());

        long userId = userService.getByEmail(registrationDTO.getEmail()).getId();
        log.info("User with id = " + userId + " was registered, got new accessToken, refreshToken");

        return new TokensDTO(userId, accessToken, refreshToken);
    }

    @PostMapping("/access")
    public TokensDTO getNewAccessToken(@RequestBody TokensDTO tokensDTO) {
        TokensDTO responseTokensDTO = new TokensDTO();
        responseTokensDTO.setAccessToken(jwtService.getNewAccessToken(tokensDTO.getRefreshToken()));

        String userEmail = jwtService.getRefreshClaims(tokensDTO.getRefreshToken()).getSubject();
        long userId = userService.getByEmail(userEmail).getId();
        log.info("User with id = " + userId + " got new accessToken");

        return responseTokensDTO;
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

    @GetMapping("/access/email")
    public Map<String, String> getEmailFromAccessToken(@RequestParam String accessToken) {
        Map<String, String> res = new HashMap<>();
        res.put("email", jwtService.getAccessClaims(accessToken).getSubject());

        return res;
    }
}
