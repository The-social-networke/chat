package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ApiModel(description = "dto to read message")
public class MessageReadDto {

    @NotNull(message = "message id should not be null")
    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "message that should be read")
    private String messageId;

    @JsonIgnore
    private String currentUserId;
}
