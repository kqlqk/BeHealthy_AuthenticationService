package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.model.User;
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

        mockMvc.perform(put("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_shouldReturnJsonWithException() throws Exception {
        mockMvc.perform(put("/api/v1/users/1")
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

}