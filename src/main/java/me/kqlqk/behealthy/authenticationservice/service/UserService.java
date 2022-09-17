package me.kqlqk.behealthy.authenticationservice.service;

import me.kqlqk.behealthy.authenticationservice.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User getById(long id);
}
