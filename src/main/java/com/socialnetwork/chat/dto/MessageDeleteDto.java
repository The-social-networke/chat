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
@ApiModel(description = "dto to delete message")
public class MessageDeleteDto {

    @NotNull(message = "id should not be null")
    @ApiModelProperty(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7")
    private String messageId;

    @JsonIgnore
    private String currentUserId;
}
