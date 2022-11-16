package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.model.RefreshToken;
import me.kqlqk.behealthy.authentication_service.model.User;
import me.kqlqk.behealthy.authentication_service.service.impl.TokenServiceImpl;
import me.kqlqk.behealthy.authentication_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class UserRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenServiceImpl tokenService;

    @Autowired
    private UserServiceImpl userService;


    @Test
    public void getUserById_shouldReturnJsonWithUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void createUser_shouldCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Steve", "stve@mail.com", "randomPSWD1");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void createUser_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Required request body is missing")));
    }

    @Test
    public void updateUser_shouldUpdateUser() throws Exception {
        User user = userService.getById(1);
        user.setName(user.getName() + "123");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(user);

        mockMvc.perform(patch("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Required request body is missing")));
    }

    @Test
    public void getAllUsersOrSpecified_shouldReturnAllUsersOrSpecified() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.size()", is(2)));

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "john@mail.com"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void getNewAccessToken_shouldReturnNewAccessToken() throws Exception {
        mockMvc.perform(put("/api/v1/users/1/access")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access").exists());
    }

    @Test
    public void getNewAccessToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(put("/api/v1/users/99/access")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("User with id = 99 not found")));
    }

    @Test
    public void getNewRefreshToken_shouldGetRefreshToken() throws Exception {
        mockMvc.perform(put("/api/v1/users/1/refresh")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.refresh").exists());
    }

    @Test
    public void getNewRefreshToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(put("/api/v1/users/99/refresh")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("User with id = 99 not found")));
    }

    @Test
    public void updateTokens_shouldUpdateAccessAndRefreshTokens() throws Exception {
        mockMvc.perform(put("/api/v1/users/1/tokens")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access").exists())
                .andExpect(jsonPath("$.refresh").exists());
    }

    @Test
    public void updateTokens_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(put("/api/v1/users/99/tokens")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("User with id = 99 not found")));
    }

    @Test
    public void validateAccessToken_shouldValidateAccessToken() throws Exception {
        String token = tokenService.createAccessToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/validate/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_" + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.valid").exists())
                .andExpect(jsonPath("$.valid", is(true)));

        mockMvc.perform(get("/api/v1/auth/validate/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.valid").exists())
                .andExpect(jsonPath("$.valid", is(false)));

    }

    @Test
    public void validateAccessToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validate/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Access token should starts with Bearer_")));

        mockMvc.perform(get("/api/v1/auth/validate/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("header", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("Authorization_access header not found")));

    }

    @Test
    public void validateRefreshToken_shouldValidateRefreshToken() throws Exception {
        RefreshToken refreshToken = tokenService.createRefreshToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/validate/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_" + refreshToken.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.valid").exists())
                .andExpect(jsonPath("$.valid", is(true)));

        mockMvc.perform(get("/api/v1/auth/validate/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.valid").exists())
                .andExpect(jsonPath("$.valid", is(false)));
    }

    @Test
    public void validateRefreshToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validate/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Refresh token should starts with Bearer_")));

        mockMvc.perform(get("/api/v1/auth/validate/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("header", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Authorization_refresh header not found")));

    }

    @Test
    public void getAccessTokenFromRequest_shouldReturnAccessTokenFromRequest() throws Exception {
        String token = tokenService.createAccessToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/request/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_" + token))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access").exists())
                .andExpect(jsonPath("$.access", is(token)));

    }

    @Test
    public void getAccessTokenFromRequest_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/request/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "token"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Access token should starts with Bearer_")));

        mockMvc.perform(get("/api/v1/auth/request/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("header", "Bearer_token"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Authorization_access header not found")));
    }

    @Test
    public void getRefreshTokenFromRequest_shouldReturnRefreshTokenFromRequest() throws Exception {
        RefreshToken token = tokenService.createRefreshToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/request/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_" + token.getToken()))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.refresh").exists())
                .andExpect(jsonPath("$.refresh", is(token.getToken())));

    }

    @Test
    public void getRefreshTokenFromRequest_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/request/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "token"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Refresh token should starts with Bearer_")));

        mockMvc.perform(get("/api/v1/auth/request/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("header", "Bearer_token"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Authorization_refresh header not found")));
    }

    @Test
    public void getEmailFromAccessToken_shouldReturnEmailFromAccessToken() throws Exception {
        String token = tokenService.createAccessToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/request/access/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_" + token))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email", is("john@mail.com")));
    }

    @Test
    public void getEmailFromAccessToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/request/access/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_token"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Access token cannot be parsed")));
    }

    @Test
    public void getEmailFromRefreshToken_shouldReturnEmailFromRefreshToken() throws Exception {
        RefreshToken token = tokenService.createRefreshToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/request/refresh/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_" + token.getToken()))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email", is("john@mail.com")));
    }

    @Test
    public void getEmailFromRefreshToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/request/refresh/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_token"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Refresh token cannot be parsed")));
    }
}