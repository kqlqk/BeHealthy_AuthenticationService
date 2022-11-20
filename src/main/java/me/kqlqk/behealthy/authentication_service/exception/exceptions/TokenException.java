package me.kqlqk.behealthy.authentication_service.exception.exceptions;

public class TokenException extends RuntimeException {
    private static final String TOKEN = "Token";

    public TokenException(String message) {
        super(TOKEN + " | " + message);
    }
}
