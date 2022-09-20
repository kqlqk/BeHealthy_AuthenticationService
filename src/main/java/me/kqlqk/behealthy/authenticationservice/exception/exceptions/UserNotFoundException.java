package me.kqlqk.behealthy.authenticationservice.exception.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
