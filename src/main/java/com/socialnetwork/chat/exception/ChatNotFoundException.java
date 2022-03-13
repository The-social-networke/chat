package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class ChatNotFoundException extends EntityException {

    public ChatNotFoundException() {
        super(ErrorCodeException.CHAT_NOT_FOUND);
    }
}
