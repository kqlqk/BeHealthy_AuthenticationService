package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.CheckPasswordDTO;
import me.kqlqk.behealthy.authentication_service.dto.userDTO.UpdateUserDTO;
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

    @Test
    public void getUserById_shouldReturnJsonWithUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    public void getUserById_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/users/99")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("User with id = 99 not found")));
    }

    @Test
    public void updateUser_shouldUpdateUser() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName("newName");
        updateUserDTO.setEmail("newEmail@mail.com");
        updateUserDTO.setPassword("newPSWD1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_shouldReturnJsonWithException() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        ObjectMapper objectMapper = new ObjectMapper();

        updateUserDTO.setName("22");
        updateUserDTO.setEmail("newEmail@mail.com");
        updateUserDTO.setPassword("newPSWD1234");
        String jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name should contains only letters")));

        updateUserDTO.setName("c");
        updateUserDTO.setEmail("newEmail@mail.com");
        updateUserDTO.setPassword("newPSWD1234");
        jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name should be between 2 and 20 characters")));

        updateUserDTO.setName(null);
        updateUserDTO.setEmail("newEmail@mail.com");
        updateUserDTO.setPassword("newPSWD1234");
        jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Name cannot be null")));


        updateUserDTO.setName("newName");
        updateUserDTO.setEmail("badEmail");
        updateUserDTO.setPassword("newPSWD1234");
        jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Email should be valid")));

        updateUserDTO.setName("newName");
        updateUserDTO.setEmail(null);
        updateUserDTO.setPassword("newPSWD1234");
        jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Email cannot be null")));


        updateUserDTO.setName("newName");
        updateUserDTO.setEmail("newEmail@mail.com");
        updateUserDTO.setPassword("badPswd");
        jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Password should be between 8 and 50 characters," +
                                                         " no spaces, at least: 1 number, 1 uppercase letter, 1 lowercase letter")));

        updateUserDTO.setName("newName");
        updateUserDTO.setEmail("newEmail@mail.com");
        updateUserDTO.setPassword(null);
        jsonUpdateUserDTO = objectMapper.writeValueAsString(updateUserDTO);
        mockMvc.perform(put("/api/v1/users/1")
                                .content(jsonUpdateUserDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Password cannot be null")));
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
                                .param("email", "user1@mail.com"))
                .andDo(print())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void getAllUsersOrSpecified_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("email", "random"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("User with email = random not found")));
    }

    @Test
    public void checkUserPassword_shouldCheckUserPassword() throws Exception {
        CheckPasswordDTO checkPasswordDTO = new CheckPasswordDTO();
        checkPasswordDTO.setPassword("Test1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonCheckPasswordDTO = objectMapper.writeValueAsString(checkPasswordDTO);

        mockMvc.perform(post("/api/v1/users/1/password/check")
                                .content(jsonCheckPasswordDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.valid").exists())
                .andExpect(jsonPath("$.valid", is(true)));
    }

    @Test
    public void checkUserPassword_shouldReturnJsonWithException() throws Exception {
        CheckPasswordDTO checkPasswordDTO = new CheckPasswordDTO();
        checkPasswordDTO.setPassword("random");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonCheckPasswordDTO = objectMapper.writeValueAsString(checkPasswordDTO);

        mockMvc.perform(post("/api/v1/users/99/password/check")
                                .content(jsonCheckPasswordDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("User with id = 99 not found")));

        checkPasswordDTO.setPassword(null);
        jsonCheckPasswordDTO = objectMapper.writeValueAsString(checkPasswordDTO);
        mockMvc.perform(post("/api/v1/users/99/password/check")
                                .content(jsonCheckPasswordDTO)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info", is("Password cannot be null")));
    }
}