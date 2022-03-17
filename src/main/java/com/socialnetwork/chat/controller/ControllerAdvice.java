package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.dto.ErrorDto;
import com.socialnetwork.chat.dto.ErrorViolationDto;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.util.FieldViolationsError;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import org.springframework.http.HttpStatus;
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
    public ErrorViolationDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
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

        return ErrorViolationDto.builder()
            .message("Unable to process entity")
            .errorCode(ErrorCodeException.NOT_VALID_PARAM)
            .violations(violations)
            .build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorDto handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {

        return ErrorDto.builder()
            .message(ex.getMessage())
            .errorCode(ErrorCodeException.MISSING_ARGUMENT)
            .build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatException.class)
    public ErrorDto handleNotFoundError(ChatException ex) {

        return ErrorDto.builder()
            .message(ex.getMessage())
            .errorCode(ex.getErrorCodeException())
            .build();
    }
}
