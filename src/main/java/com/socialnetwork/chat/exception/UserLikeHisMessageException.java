package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class UserLikeHisMessageException extends EntityException {

    public UserLikeHisMessageException() {
        super(ErrorCodeException.USER_CANNOT_LIKE_HIS_MESSAGE);
    }
}
