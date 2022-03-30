package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ApiModel(description = "dto to create chatRoom")
public class ChatRoomCreateDto {

    @NotEmpty(message = "user should be present")
    @ApiModelProperty(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        notes = "Another user in chat room")
    private String userId;

    @JsonIgnore
    private String currentUserId;
}
