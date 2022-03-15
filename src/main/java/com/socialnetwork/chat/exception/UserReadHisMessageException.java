package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class UserReadHisMessageException extends EntityException {

    public UserReadHisMessageException() {
        super(ErrorCodeException.USER_CANNOT_READ_HIS_MESSAGE);
    }
}
