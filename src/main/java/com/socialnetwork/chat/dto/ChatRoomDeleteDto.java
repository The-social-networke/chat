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
@ApiModel(description = "dto to delete chatRoom")
public class ChatRoomDeleteDto {

    @NotNull(message = "id should not be null")
    @ApiModelProperty(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Chat id that should be deleted")
    private String chatId;

    @JsonIgnore
    private String currentUserId;
}
