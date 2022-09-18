package integration.authenticationservice.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authenticationservice.dto.TokensDTO;
import me.kqlqk.behealthy.authenticationservice.dto.UserDTO;
import me.kqlqk.behealthy.authenticationservice.model.RefreshToken;
import me.kqlqk.behealthy.authenticationservice.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
        userDTO.setPassword("randomPSWD");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_shouldUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("john");
        userDTO.setEmail("new_mail@mail.com");
        userDTO.setAge((byte) 55);
        userDTO.setPassword("randomPSWD");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
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
    public void getRefreshToken_shouldGetRefreshToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/new_refresh_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.refresh").exists());
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
    public void validateAccessToken_shouldValidateAccessToken() throws Exception {
        String token = tokenService.createAccessToken("john@mail.com");

        TokensDTO tokensDTO = new TokensDTO();
        tokensDTO.setAccessToken(token);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonTokenDTO = objectMapper.writeValueAsString(tokensDTO);

        mockMvc.perform(post("/api/v1/auth/validate_access_token")
                        .content(jsonTokenDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


        tokensDTO.setAccessToken("token");
        jsonTokenDTO = objectMapper.writeValueAsString(tokensDTO);

        mockMvc.perform(post("/api/v1/auth/validate_access_token")
                        .content(jsonTokenDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void validateRefreshToken_shouldValidateRefreshToken() throws Exception {
        RefreshToken token = tokenService.createAndSaveRefreshToken("john@mail.com");

        TokensDTO tokensDTO = new TokensDTO();
        tokensDTO.setRefreshToken(token.getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonTokenDTO = objectMapper.writeValueAsString(tokensDTO);

        mockMvc.perform(post("/api/v1/auth/validate_refresh_token")
                        .content(jsonTokenDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


        tokensDTO.setRefreshToken("token");
        jsonTokenDTO = objectMapper.writeValueAsString(tokensDTO);

        mockMvc.perform(post("/api/v1/auth/validate_refresh_token")
                        .content(jsonTokenDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }
}