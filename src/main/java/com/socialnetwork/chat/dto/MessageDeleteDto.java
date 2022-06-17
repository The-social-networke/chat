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
@Tag(name = "", description = "dto to delete message")
public class MessageDeleteDto {

    @NotNull(message = "id should not be null")
    @Schema(
        example = "cfdbefcb-012e-4901-97e1-c673335558d7")
    private String messageId;

    @JsonIgnore
    private String currentUserId;
}
