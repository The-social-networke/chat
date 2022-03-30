package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.util.enums.ForwardType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ApiModel(description = "dto to update message")
public class MessageUpdateDto {

    @ApiModelProperty(required = true,
        example = "some text",
        notes = "text of message")
    private String text;

    @NotNull(message = "message id should not be null")
    @ApiModelProperty(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Message that should be deleted")
    private String messageId;

    @JsonIgnore
    private String currentUserId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
