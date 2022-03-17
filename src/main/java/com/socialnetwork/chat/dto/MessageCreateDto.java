package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.enums.ForwardType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageCreateDto {

    private String text;

    @NotNull(message = "chat room id should not be null")
    private String chatRoomId;

    @NotNull(message = "user id should not be null")
    private String userId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
