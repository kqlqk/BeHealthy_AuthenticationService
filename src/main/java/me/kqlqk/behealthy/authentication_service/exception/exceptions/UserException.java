package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class UserException extends RuntimeException {
    private static final String USER_EXCEPTION = "User";

    public UserException(String message) {
        super(USER_EXCEPTION + " | " + message);
    }
}
