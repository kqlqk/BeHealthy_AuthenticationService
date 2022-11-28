package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class TokenNotFoundException extends RuntimeException {
    private static final String TOKEN_NOT_FOUND = "TokenNotFound";

    public TokenNotFoundException(String message) {
        super(TOKEN_NOT_FOUND + " | " + message);
    }
}
