package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.enums.MessageStatus;
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
public class ChatRoomMessageStatusDto {

    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Another user in chat room")
    private String chatRoomId;

    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Sent by user in chat room")
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
        notes = "Status of last message")
    private MessageStatus messageStatus;
}
