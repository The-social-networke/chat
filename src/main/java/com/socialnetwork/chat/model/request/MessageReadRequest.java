package com.socialnetwork.chat.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to read message")
public class MessageReadRequest {

    @NotNull(message = "message id should not be null")
    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "message that should be read")
    private String messageId;
}
