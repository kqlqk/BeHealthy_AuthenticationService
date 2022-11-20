package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String USER_ALREADY_EXISTS = "UserAlreadyExists";

    public UserAlreadyExistsException(String message) {
        super(USER_ALREADY_EXISTS + " | " + message);
    }
}
