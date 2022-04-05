package com.socialnetwork.chat.util.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodeException {
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),

    MISSING_ARGUMENT(500, "missing argument"),
    NOT_VALID_PARAM(501, "not valid"),

    USER_NOT_FOUND(1000, "user not found"),
    CHAT_NOT_FOUND(1001, "chat not found"),
    NOT_MEMBER_OF_CHAT(1002, "not member of chat"),
    CHAT_WITH_THESE_USERS_ALREADY_EXISTS(1003, "chat with these users already exits"),
    USER_CANNOT_LIKE_HIS_MESSAGE(1004, "user cannot like himself"),
    USER_CANNOT_READ_HIS_MESSAGE(1005, "user cannot read his message"),
    USER_CANNOT_DELETE_NOT_OWN_MESSAGE(1006, "user cannot delete not own message"),
    USER_CANNOT_UPDATE_NOT_OWN_MESSAGE(1007, "user cannot update not own message");

    @JsonValue
    private final int code;

    @JsonIgnore
    private final String message;
}
