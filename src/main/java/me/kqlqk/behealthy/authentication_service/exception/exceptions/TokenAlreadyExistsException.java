package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class TokenAlreadyExistsException extends RuntimeException {
    public TokenAlreadyExistsException(String message) {
        super(message);
    }
}
