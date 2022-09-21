package me.kqlqk.behealthy.authenticationservice.dto;

import me.kqlqk.behealthy.authenticationservice.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String password;
    private byte age;

    public UserDTO() {
    }

    public UserDTO(long id, String name, String email, String password, byte age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
    }

    public static List<UserDTO> convertListOfUsersToListOfUserDTOs(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getAge());
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

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }
}
