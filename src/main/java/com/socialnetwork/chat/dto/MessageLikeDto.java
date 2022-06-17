package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to like message")
public class MessageLikeDto {

    @NotNull(message = "isLike should not be null")
    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "like or dislike of message")
    private Boolean isLike;

    @NotNull(message = "message id should not be null")
    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "message that should be liked")
    private String messageId;

    @JsonIgnore
    private String currentUserId;
}
