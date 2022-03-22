package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.util.enums.ForwardType;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageCreateDto {

    private String text;

    @NotNull(message = "chat room id should not be null")
    private String chatRoomId;

    @JsonIgnore
    private String currentUserId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
