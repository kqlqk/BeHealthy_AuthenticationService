package me.kqlqk.behealthy.authenticationservice.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDTO {
    @Pattern(regexp = "[a-zA-z]*", message = "Name should contains only letters")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 letters")
    private String name;

    @Pattern(regexp = "^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$", message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50}$",
            message = "Password must be between 8 and 50 characters, at least: 1 number, 1 uppercase letter, 1 lowercase letter")
    private String password;

    @Min(value = 3, message = "Age should be between 3 and 120")
    @Max(value = 120, message = "Age should be between 3 and 120")
    private byte age;

    public UserDTO() {
    }

    public UserDTO(String name, String email, String password, byte age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
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
