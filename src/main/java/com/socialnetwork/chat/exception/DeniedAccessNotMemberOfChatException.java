package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class DeniedAccessNotMemberOfChatException extends EntityException {

    public DeniedAccessNotMemberOfChatException(String userId) {
        super(ErrorCodeException.NOT_MEMBER_OF_CHAT);
    }
}
