package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class AuthRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void login_shouldReturnTokens() throws Exception {
        UserDTO validUserDTO = new UserDTO("john@mail.com", "Test1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validUserDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void login_shouldReturnJsonWithException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String invalidPassword = "invalidPSWD123";
        UserDTO invalidUserDTO = new UserDTO("john@mail.com", invalidPassword);
        String invalidJson = objectMapper.writeValueAsString(invalidUserDTO);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("UserNotFound | Bad credentials")));


        String invalidMail = "invalidMail@mail.com";
        UserDTO invalidUserDTO2 = new UserDTO(invalidMail, "Test1234");
        String invalidJson2 = objectMapper.writeValueAsString(invalidUserDTO2);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("UserNotFound | Bad credentials")));
    }

    @Test
    public void registration_shouldCreateUserAndReturnTokens() throws Exception {
        UserDTO validUserDTO = new UserDTO("NewUser", "newUser@mail.com", "Test1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validUserDTO);

        int oldSize = userRepository.findAll().size();

        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        int newSize = userRepository.findAll().size();
        assertThat(oldSize).isEqualTo(newSize - 1);
    }


    @Test
    public void getNewAccessToken_shouldReturnNewAccessToken() throws Exception {
        TokensDTO validTokensDTO = new TokensDTO(null,
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQG1haWwuY29tIiwiZXhwIjoxNjczMTc3MzQ2fQ.-2omlZ2c_TPjbBVUb2ODBm_z8cvuknyLRwFZc47aXvHDjurWAQeqksAYKjmYr_nb");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validTokensDTO);

        mockMvc.perform(post("/api/v1/auth/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists());


        TokensDTO invalidTokensDTO = new TokensDTO(null,
                "randomToken");
        String invalidJson = objectMapper.writeValueAsString(invalidTokensDTO);
        mockMvc.perform(post("/api/v1/auth/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Token | Token Malformed")));
    }

    @Test
    public void updateTokens_shouldUpdateTokens() throws Exception {
        TokensDTO validTokensDTO = new TokensDTO(null,
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQG1haWwuY29tIiwiZXhwIjoxNjczMTc3MzQ2fQ.-2omlZ2c_TPjbBVUb2ODBm_z8cvuknyLRwFZc47aXvHDjurWAQeqksAYKjmYr_nb");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validTokensDTO);

        mockMvc.perform(post("/api/v1/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());


        TokensDTO invalidTokensDTO = new TokensDTO(null,
                "randomToken");
        String invalidJson = objectMapper.writeValueAsString(invalidTokensDTO);
        mockMvc.perform(post("/api/v1/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Token | Token Malformed")));
    }
}

