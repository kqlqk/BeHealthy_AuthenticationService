package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.TokensDTO;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.LoginDTO;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.RegistrationDTO;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.UserDTO;
import me.kqlqk.behealthy.authentication_service.repository.UserRepository;
import me.kqlqk.behealthy.authentication_service.service.JWTService;
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

    @Autowired
    private JWTService jwtService;

    @Test
    public void login_shouldReturnTokens() throws Exception {
        LoginDTO validLoginDTO = new LoginDTO("user1@mail.com", "Test1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validLoginDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void login_shouldReturnJsonWithException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String invalidPassword = "invalidPSWD123";
        LoginDTO invalidLoginDTO = new LoginDTO("user1@mail.com", invalidPassword);
        String invalidJson = objectMapper.writeValueAsString(invalidLoginDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Bad credentials")));


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
                .andExpect(jsonPath("$.info", is("Bad credentials")));
    }

    @Test
    public void registration_shouldCreateUserAndReturnTokens() throws Exception {
        RegistrationDTO validRegistrationDTO = new RegistrationDTO("NewUser", "newUser@mail.com", "Test1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validRegistrationDTO);

        int size = userRepository.findAll().size();

        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        int newSize = userRepository.findAll().size();

        assertThat(newSize).isEqualTo(size + 1);
    }

    @Test
    public void registration_shouldReturnJsonWithException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RegistrationDTO invalidRegistrationDTO = new RegistrationDTO("123", "newUser@mail.com", "Test1234");
        String invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name should contains only letters")));

        invalidRegistrationDTO = new RegistrationDTO("a", "newUser@mail.com", "Test1234");
        invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name should be between 2 and 20 characters")));

        invalidRegistrationDTO = new RegistrationDTO(null, "newUser@mail.com", "Test1234");
        invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name cannot be null")));


        invalidRegistrationDTO = new RegistrationDTO("name", "badEmail", "Test1234");
        invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Email should be valid")));

        invalidRegistrationDTO = new RegistrationDTO("name", null, "Test1234");
        invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Email cannot be null")));


        invalidRegistrationDTO = new RegistrationDTO("name", "newUser@mail.com", "badPswd");
        invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Password should be between 8 and 50 characters," +
                                                         " no spaces, at least: 1 number, 1 uppercase letter, 1 lowercase letter")));

        invalidRegistrationDTO = new RegistrationDTO("name", "newUser@mail.com", null);
        invalidJson = objectMapper.writeValueAsString(invalidRegistrationDTO);
        mockMvc.perform(post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Password cannot be null")));
    }


    @Test
    public void getNewAccessToken_shouldReturnNewAccessToken() throws Exception {
        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken("user1@mail.com");

        TokensDTO validTokensDTO = new TokensDTO(null, refreshToken);
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = objectMapper.writeValueAsString(validTokensDTO);

        mockMvc.perform(post("/api/v1/auth/access")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    public void getNewAccessToken_shouldReturnJsonWithException() throws Exception {
        TokensDTO invalidTokensDTO = new TokensDTO(null, "randomToken");
        ObjectMapper objectMapper = new ObjectMapper();
        String invalidJson = objectMapper.writeValueAsString(invalidTokensDTO);

        mockMvc.perform(post("/api/v1/auth/access")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Token Malformed")));
    }

    @Test
    public void updateTokens_shouldUpdateTokens() throws Exception {
        String refreshToken = jwtService.generateAndSaveOrUpdateRefreshToken("user1@mail.com");

        TokensDTO validTokensDTO = new TokensDTO(null, refreshToken);
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
    }

    @Test
    public void updateTokens_shouldReturnJsonWithException() throws Exception {
        TokensDTO invalidTokensDTO = new TokensDTO(null, "randomToken");
        ObjectMapper objectMapper = new ObjectMapper();
        String invalidJson = objectMapper.writeValueAsString(invalidTokensDTO);

        mockMvc.perform(post("/api/v1/auth/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Token Malformed")));
    }
}

