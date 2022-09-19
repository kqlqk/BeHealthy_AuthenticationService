package integration.authenticationservice.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authenticationservice.dto.UserDTO;
import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class UserRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenServiceImpl tokenService;

    @Test
    public void getUser_shouldReturnJsonWithUser() throws Exception {
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
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Steve");
        userDTO.setEmail("steve@mail.com");
        userDTO.setAge((byte) 19);
        userDTO.setPassword("randomPSWD1");

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
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("$.info", is("Required request body is missing")));


        UserDTO userDTO = new UserDTO("  ", "stee@mail.com", "randomPSWD1", (byte) 19);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Name should contains only letters")));


        userDTO = new UserDTO("a", "steve@mail.com", "randomPSWD1", (byte) 19);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Name should be between 2 and 20 letters")));


        userDTO = new UserDTO("Steve", "badMail.com", "randomPSWD1", (byte) 19);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Email should be valid")));


        userDTO = new UserDTO("Steve", "steve@mail.com", "random", (byte) 19);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info",
                        is("Password must be between 8 and 50 characters, at least: 1 number, 1 uppercase letter, 1 lowercase letter")));


        userDTO = new UserDTO("Steve", "steve@mail.com", "randomPSWD1", (byte) 1);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Age should be between 3 and 120")));
    }

    @Test
    public void updateUser_shouldUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("john");
        userDTO.setEmail("new_mail@mail.com");
        userDTO.setAge((byte) 55);
        userDTO.setPassword("randomPSWD1");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(post("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("$.info", is("Required request body is missing")));


        UserDTO userDTO = new UserDTO("  ", "stee@mail.com", "randomPSWD1", (byte) 19);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Name should contains only letters")));


        userDTO = new UserDTO("a", "steve@mail.com", "randomPSWD1", (byte) 19);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Name should be between 2 and 20 letters")));


        userDTO = new UserDTO("Steve", "badMail.com", "randomPSWD1", (byte) 19);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Email should be valid")));


        userDTO = new UserDTO("Steve", "steve@mail.com", "random", (byte) 19);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info",
                        is("Password must be between 8 and 50 characters, at least: 1 number, 1 uppercase letter, 1 lowercase letter")));


        userDTO = new UserDTO("Steve", "steve@mail.com", "randomPSWD1", (byte) 1);
        objectMapper = new ObjectMapper();
        jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("info").exists())
                .andExpect(jsonPath("info", is("Age should be between 3 and 120")));
    }

    @Test
    public void getAccessToken_shouldGetAccessToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/new_access_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access").exists());
    }

    @Test
    public void getAccessToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/users/99/new_access_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("User with id = 99 not found")));
    }

    @Test
    public void getRefreshToken_shouldGetRefreshToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/new_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.refresh").exists());
    }

    @Test
    public void getRefreshToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/users/99/new_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("User with id = 99 not found")));
    }

    @Test
    public void updateTokens_shouldUpdateAccessAndRefreshTokens() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/update_tokens")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access").exists())
                .andExpect(jsonPath("$.refresh").exists());
    }

    @Test
    public void updateTokens_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/users/99/update_tokens")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("User not found")));
    }

    @Test
    public void validateAccessToken_shouldValidateAccessToken() throws Exception {
        String token = tokenService.createAccessToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/validate_access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_" + token))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/auth/validate_access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void validateAccessToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validate_access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_access", "token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("Access token should starts with Bearer_")));

        mockMvc.perform(get("/api/v1/auth/validate_access_token")
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
        RefreshToken refreshToken = tokenService.createAndSaveRefreshToken("john@mail.com");

        mockMvc.perform(get("/api/v1/auth/validate_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_" + refreshToken.getToken()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/auth/validate_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void validateRefreshToken_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/auth/validate_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization_refresh", "token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("Refresh token should starts with Bearer_")));

        mockMvc.perform(get("/api/v1/auth/validate_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("header", "Bearer_token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("info", is("Authorization_refresh header not found")));

    }
}