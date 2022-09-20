package me.kqlqk.behealthy.authenticationservice.exception;

import me.kqlqk.behealthy.authenticationservice.dto.ExceptionDTO;
import me.kqlqk.behealthy.authenticationservice.exception.exceptions.TokenException;
import me.kqlqk.behealthy.authenticationservice.exception.exceptions.UserAlreadyExistsException;
import me.kqlqk.behealthy.authenticationservice.exception.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class, UserNotFoundException.class, TokenException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handle(Exception e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setInfo(e.getMessage());

        return exceptionDTO;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleHttpMessageNotReadableEx() {
        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setInfo("Required request body is missing");

        return exceptionDTO;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNonValidDataEx(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> errors.put("info", error.getDefaultMessage()));

        return errors;
    }
}
