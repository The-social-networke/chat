package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.util.enums.ForwardType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to create message")
public class MessageCreateDto {

    @Schema(required = true,
        example = "some text",
        description = "text of message")
    private String text;

    @NotNull(message = "chat room id should not be null")
    @Schema(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Another user in chat room")
    private String chatRoomId;

    @JsonIgnore
    private String currentUserId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
