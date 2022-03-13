package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.Getter;

@Getter
public abstract class EntityException extends RuntimeException {

    protected final ErrorCodeException errorCodeException;

    protected EntityException(ErrorCodeException code) {
        super(code.getMessage());
        errorCodeException = code;
    }
}
