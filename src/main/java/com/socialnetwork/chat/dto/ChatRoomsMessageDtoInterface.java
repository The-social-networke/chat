package com.socialnetwork.chat.dto;

import java.time.LocalDateTime;

public interface ChatRoomsMessageDtoInterface {
    String getChatRoomId();

    String getUserId();

    String getMessageId();

    String getText();

    LocalDateTime getSentAt();
}
