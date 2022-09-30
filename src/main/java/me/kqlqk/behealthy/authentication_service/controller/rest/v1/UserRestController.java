package me.kqlqk.behealthy.authentication_service.controller.rest.v1;

import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.dto.ValidateDTO;
import me.kqlqk.behealthy.authentication_service.exception.exceptions.UserNotFoundException;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.TokenService;
import me.kqlqk.behealthy.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public UserDTO getUserById(@PathVariable long id) {
        return UserDTO.convertFromUserToUserDTO(userService.getById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        userService.create(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}")
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

    @GetMapping("/users/{id}/new_access_token")
    public Map<String, String> getAccessToken(@PathVariable long id) {
        User user = userService.getById(id);

        if (user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        Map<String, String> res = new HashMap<>();
        String accessToken = tokenService.createAccessToken(user.getEmail());
        res.put("access", accessToken);

        return res;
    }

    @GetMapping("/users/{id}/new_refresh_token")
    public Map<String, String> getRefreshToken(@PathVariable long id) {
        User user = userService.getById(id);

        if (user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        Map<String, String> res = new HashMap<>();
        RefreshToken refreshToken = tokenService.createRefreshToken(user.getEmail());
        res.put("refresh", refreshToken.getToken());

        return res;
    }

    @GetMapping("/users/{id}/update_tokens")
    public Map<String, String> updateTokens(@PathVariable long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        return tokenService.updateAccessAndRefreshToken(user.getEmail());
    }

    @GetMapping("/auth/validate_access_token")
    public ValidateDTO validateAccessToken(HttpServletRequest request) {
        String token = tokenService.getAccessTokenFromHeader(request);

        ValidateDTO validateDTO = new ValidateDTO();
        validateDTO.setValid(tokenService.isAccessTokenValid(token));

        return validateDTO;
    }

    @GetMapping("/auth/validate_refresh_token")
    public ValidateDTO validateRefreshToken(HttpServletRequest request) {
        String token = tokenService.getRefreshTokenFromHeader(request);

        ValidateDTO validateDTO = new ValidateDTO();
        validateDTO.setValid(tokenService.isRefreshTokenValid(token));

        return validateDTO;
    }

    @GetMapping("/auth/get_access_token")
    public Map<String, String> getAccessTokenFromRequest(HttpServletRequest request) {
        Map<String, String> token = new HashMap<>();

        token.put("access", tokenService.getAccessTokenFromHeader(request));

        return token;
    }

    @GetMapping("/auth/get_refresh_token")
    public Map<String, String> getRefreshTokenFromRequest(HttpServletRequest request) {
        Map<String, String> token = new HashMap<>();

        token.put("refresh", tokenService.getRefreshTokenFromHeader(request));

        return token;
    }

    @GetMapping("/auth/get_email_from_access_token")
    public Map<String, String> getEmailFromAccessToken(HttpServletRequest request) {
        Map<String, String> token = new HashMap<>();

        token.put("email", tokenService.getEmailByAccessToken(tokenService.getAccessTokenFromHeader(request)));

        return token;
    }

    @GetMapping("/auth/get_email_from_refresh_token")
    public Map<String, String> getEmailFromRefreshToken(HttpServletRequest request) {
        Map<String, String> token = new HashMap<>();

        token.put("email", tokenService.getEmailByRefreshToken(tokenService.getRefreshTokenFromHeader(request)));

        return token;
    }
}
