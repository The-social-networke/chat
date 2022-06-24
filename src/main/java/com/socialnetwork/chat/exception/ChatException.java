package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.model.enums.ErrorCodeException;
import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    private static final long serialVersionUID = 4417445544800001298L;
    protected final ErrorCodeException errorCodeException;

    public ChatException(ErrorCodeException code) {
        super(code.getMessage());
        errorCodeException = code;
    }
}
