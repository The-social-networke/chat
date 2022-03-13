package com.socialnetwork.chat.exception;

import com.socialnetwork.chat.util.enums.ErrorCodeException;

public class ChatWithTheseUsersAlreadyExists extends EntityException {

    public ChatWithTheseUsersAlreadyExists() {
        super(ErrorCodeException.CHAT_WITH_THESE_USERS_ALREADY_EXISTS);
    }
}
