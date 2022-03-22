package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageLikeDto {

    @NotNull(message = "isLike should not be null")
    private Boolean isLike;

    @NotNull(message = "message id should not be null")
    private String messageId;

    @JsonIgnore
    private String currentUserId;
}
