package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    protected final ErrorCodeException errorCodeException;

    public ChatException(ErrorCodeException code) {
        super(code.getMessage());
        errorCodeException = code;
    }
}
