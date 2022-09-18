package me.kqlqk.behealthy.authenticationservice.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
