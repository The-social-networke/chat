package com.socialnetwork.chat.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageLikeDto {

    @NotNull(message = "isLike should not be null")
    private Boolean isLike;

    @NotNull(message = "message id should not be null")
    private String messageId;

    @Null(message = "user id should not be null")
    private String userId;
}
