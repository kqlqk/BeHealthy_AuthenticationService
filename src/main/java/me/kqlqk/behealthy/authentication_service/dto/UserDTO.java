package me.kqlqk.behealthy.authentication_service.dto;

import me.kqlqk.behealthy.authentication_service.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String password;

    public UserDTO() {
    }

    public UserDTO(long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static List<UserDTO> convertListOfUsersToListOfUserDTOs(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPassword());
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
