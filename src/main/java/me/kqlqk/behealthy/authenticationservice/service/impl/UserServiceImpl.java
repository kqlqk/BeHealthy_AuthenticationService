package me.kqlqk.behealthy.authenticationservice.service.impl;

import me.kqlqk.behealthy.authenticationservice.model.User;
import me.kqlqk.behealthy.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getById(long id) {
        User user = new User();
        user.setId(1);
        user.setName("John");
        return user;
    }
}
