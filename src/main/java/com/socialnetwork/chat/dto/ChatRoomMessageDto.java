package com.socialnetwork.chat.dto;

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
@Tag(name = "", description = "dto to show chatRoom")
public class ChatRoomMessageDto {

    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Another user in chat room")
    private String chatRoomId;

    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Another user in this chat")
    private String anotherUserId;

    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Author of message")
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
        description = "Amount of not read messages")
    private Long amountOfNotReadMessages;

    @Schema(
        example = "{\"id\": \"123-123-123\", \"username\":\"some name\"}",
        description = "User information")
    private Object userInfo;

    public ChatRoomMessageDto(String chatRoomId, String anotherUserId, String userId, String messageId, String text, LocalDateTime sentAt) {
        this.chatRoomId = chatRoomId;
        this.anotherUserId = anotherUserId;
        this.userId = userId;
        this.messageId = messageId;
        this.text = text;
        this.sentAt = sentAt;
        //this.amountOfNotReadMessages = amountOfNotReadMessages;
    }
}
