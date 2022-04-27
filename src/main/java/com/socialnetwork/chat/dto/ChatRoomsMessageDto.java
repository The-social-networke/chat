package com.socialnetwork.chat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(description = "dto to show chatRoom")
public class ChatRoomsMessageDto {

    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Another user in chat room")
    private String chatRoomId;

    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Another user in this chat")
    private String anotherUserId;

    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Author of message")
    private String userId;

    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Last message id")
    private String messageId;

    @ApiModelProperty(
        example = "some text",
        notes = "Text of last message")
    private String text;

    @ApiModelProperty(
        example = "2022-03-26T02:02:42.774980",
        notes = "Last message sent time")
    private LocalDateTime sentAt;

    @ApiModelProperty(
        example = "2022-03-26T02:02:42.774980",
        notes = "Amount of not read messages")
    private Integer amountOfNotReadMessages;

    private Object userInfo;
}
