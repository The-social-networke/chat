package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.model.enums.ErrorCodeException;
import com.socialnetwork.chat.model.response.ErrorResponse;
import com.socialnetwork.chat.model.response.ErrorViolationRequest;
import com.socialnetwork.chat.util.FieldViolationsError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorViolationRequest handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var violations = new HashMap<String, List<FieldViolationsError>>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            Object rejectedValue = fieldError.getRejectedValue();
            FieldViolationsError fieldViolationsError = FieldViolationsError.builder()
                .message(message)
                .rejectedValue(rejectedValue)
                .build();

            violations.computeIfAbsent(field, k -> new ArrayList<>());
            violations.computeIfPresent(field, (key, value) -> {
                value.add(fieldViolationsError);
                return value;
            });
        }

        return ErrorViolationRequest.builder()
            .message("Unable to process entity")
            .errorCode(ErrorCodeException.NOT_VALID_PARAM)
            .violations(violations)
            .build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {

        return ErrorResponse.builder()
            .message(ex.getMessage())
            .errorCode(ErrorCodeException.MISSING_ARGUMENT)
            .build();
    }

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<Object> handleNotFoundError(ChatException ex) {
        if(ex.getErrorCodeException().equals(ErrorCodeException.UNAUTHORIZED)) {
            return new ResponseEntity<>("unauthorized", HttpStatus.UNAUTHORIZED);
        }
        if(ex.getErrorCodeException().equals(ErrorCodeException.FORBIDDEN)) {
            return new ResponseEntity<>("forbidden", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(ErrorResponse.builder()
            .message(ex.getMessage())
            .errorCode(ex.getErrorCodeException())
            .build(), HttpStatus.BAD_REQUEST);
    }
}
