package com.socialnetwork.chat.dto;

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
}
