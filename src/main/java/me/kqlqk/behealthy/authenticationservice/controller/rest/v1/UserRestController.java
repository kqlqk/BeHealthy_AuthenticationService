package me.kqlqk.behealthy.authenticationservice.controller.rest.v1;

import me.kqlqk.behealthy.authenticationservice.dto.TokensDTO;
import me.kqlqk.behealthy.authenticationservice.dto.UserDTO;
import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.service.TokenService;
import me.kqlqk.behealthy.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserRestController {
    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public UserRestController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        userService.create(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getAge());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UserDTO userDTO) {
        userService.update(id, userDTO.getName(), userDTO.getName(), userDTO.getPassword(), userDTO.getAge());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}/new_access_token")
    public Map<String, String> getAccessToken(@PathVariable long id) {
        User user = userService.getById(id);
        String accessToken = tokenService.createAccessToken(user.getEmail());

        Map<String, String> res = new HashMap<>();
        res.put("access", accessToken);

        return res;
    }

    @GetMapping("/users/{id}/new_refresh_token")
    public Map<String, String> getRefreshToken(@PathVariable long id) {
        User user = userService.getById(id);
        RefreshToken refreshToken = tokenService.createAndSaveRefreshToken(user.getEmail());

        Map<String, String> res = new HashMap<>();
        res.put("refresh", refreshToken.getToken());

        return res;
    }

    @GetMapping("/users/{id}/update_tokens")
    public Map<String, String> updateTokens(@PathVariable long id) {
        User user = userService.getById(id);

        return tokenService.updateAccessAndRefreshToken(user);
    }

    @PostMapping("/auth/validate_access_token")
    public ResponseEntity<?> validateAccessToken(@RequestBody TokensDTO tokensDTO) {
        if (tokenService.isAccessTokenValid(tokensDTO.getAccessToken())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/auth/validate_refresh_token")
    public ResponseEntity<?> validateRefreshToken(@RequestBody TokensDTO tokensDTO) {
        if (tokenService.isRefreshTokenValid(tokensDTO.getRefreshToken())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
