package integration.authentication_service.controller.rest.v1;

import annotations.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.UserDTO;
import me.kqlqk.behealthy.authentication_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    public void updateUser_shouldUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("newName");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUserDTO = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(put("/api/v1/users/1")
                        .content(jsonUserDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
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