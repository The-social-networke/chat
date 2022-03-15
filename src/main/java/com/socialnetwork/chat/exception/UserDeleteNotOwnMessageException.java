package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class UserDeleteNotOwnMessageException extends EntityException {

    public UserDeleteNotOwnMessageException() {
        super(ErrorCodeException.USER_CANNOT_DELETE_NOT_OWN_MESSAGE);
    }
}
