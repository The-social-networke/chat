package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class UserUpdateNotOwnMessageException extends EntityException {

    public UserUpdateNotOwnMessageException() {
        super(ErrorCodeException.USER_CANNOT_UPDATE_NOT_OWN_MESSAGE);
    }
}
