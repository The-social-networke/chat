package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.enums.MessageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Tag(name = "ChatRoomMessageStatusDto", description = "dto to show chatRoom")
public class ChatRoomMessageStatusDto {

    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Another user in chat room")
    private String chatRoomId;

    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Sent by user in chat room")
    private String userId;

    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Last message id")
    private String messageId;

    @Schema(
        example = "some text",
        description = "Text of last message")
    private String text;

    @Schema(
        example = "2022-03-26T02:02:42.774980",
        description = "Last message sent time")
    private LocalDateTime sentAt;

    @Schema(
        example = "2022-03-26T02:02:42.774980",
        description = "Status of last message")
    private MessageStatus messageStatus;

    @Schema(
        example = "{\"id\": \"123-123-123\", \"username\":\"some name\"}",
        description = "User information")
    private Object userInfo;
}
