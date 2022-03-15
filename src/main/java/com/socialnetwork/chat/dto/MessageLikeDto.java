package com.socialnetwork.chat.dto;

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

    @NotNull(message = "user id should not be null")
    private String userId;
}
