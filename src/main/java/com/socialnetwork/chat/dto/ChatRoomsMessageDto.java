package com.socialnetwork.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChatRoomsMessageDto {

    private String chatRoomId;

    private String userId;

    private String messageId;

    private String text;

    private LocalDateTime sentAt;
}
