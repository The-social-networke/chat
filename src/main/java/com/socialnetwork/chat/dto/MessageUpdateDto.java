package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.util.enums.ForwardType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to update message")
public class MessageUpdateDto {

    @Schema(required = true,
        example = "some text",
        description = "text of message")
    private String text;

    @NotNull(message = "message id should not be null")
    @Schema(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Message that should be deleted")
    private String messageId;

    @JsonIgnore
    private String currentUserId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
