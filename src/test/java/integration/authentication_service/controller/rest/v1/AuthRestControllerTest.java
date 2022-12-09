package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.LoginDTO;
import me.kqlqk.behealthy.authentication_service.dto.RegistrationDTO;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
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
        LoginDTO validLoginDTO = new LoginDTO("john@mail.com", "Test1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validLoginDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());


        LoginDTO invalidLoginDTO = new LoginDTO("john@mail.com", "invalidPSWD123");
        String invalidJson = objectMapper.writeValueAsString(invalidLoginDTO);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("UserNotFound | Bad credentials")));

        LoginDTO invalidLoginDTO2 = new LoginDTO("invalidMail@mail.com", "Test1234");
        String invalidJson2 = objectMapper.writeValueAsString(invalidLoginDTO);
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
        RegistrationDTO validRegistrationDTO = new RegistrationDTO("NewUser", "newUser@mail.com", "Test1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validRegistrationDTO);

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
        assertThat(oldSize).isLessThan(newSize);


        RegistrationDTO invalidRegistrationDTO = new RegistrationDTO("John", "john@mail.com", "Test1234");
        String invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("UserAlreadyExists | User with email = john@mail.com already exists")));


        RegistrationDTO invalidRegistrationDTO2 = new RegistrationDTO("NewUser2", "newUser2@mail.com", "Test1234");
        String invalidJson2 = objectMapper.writeValueAsString(invalidRegistrationDTO2);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name should contains only letters")));


        RegistrationDTO invalidRegistrationDTO3 = new RegistrationDTO("NewUser", "newUser3@mail.com", "badpswd");
        String invalidJson3 = objectMapper.writeValueAsString(invalidRegistrationDTO3);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson3))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Password must be between 8 and 50 characters," +
                        " at least: 1 number, 1 uppercase letter, 1 lowercase letter")));


        RegistrationDTO invalidRegistrationDTO4 = new RegistrationDTO("NewUser", "badMail", "Test1234");
        String invalidJson4 = objectMapper.writeValueAsString(invalidRegistrationDTO4);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson4))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Email should be valid")));
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

