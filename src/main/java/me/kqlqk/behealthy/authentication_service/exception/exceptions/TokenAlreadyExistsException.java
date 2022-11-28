package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class TokenAlreadyExistsException extends RuntimeException {
    private static final String TOKEN_ALREADY_EXISTS = "TokenAlreadyExistsException";

    public TokenAlreadyExistsException(String message) {
        super(TOKEN_ALREADY_EXISTS + " | " + message);
    }
}
