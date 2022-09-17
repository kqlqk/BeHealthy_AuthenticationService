package me.kqlqk.behealthy.authenticationservice.controller.rest.v1;

import me.kqlqk.behealthy.authenticationservice.dto.UserDTO;
import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        userService.create(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getAge());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UserDTO userDTO) {
        userService.update(id, userDTO.getName(), userDTO.getName(), userDTO.getPassword(), userDTO.getAge());
        return ResponseEntity.ok().build();
    }


}
