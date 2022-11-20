package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND = "UserNotFound";

    public UserNotFoundException(String message) {
        super(USER_NOT_FOUND + " | " + message);
    }
}
