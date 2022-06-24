package com.socialnetwork.chat.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to delete chatRoom")
public class ChatRoomDeleteRequest {

    @NotNull(message = "id should not be null")
    @Schema(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Chat id that should be deleted")
    private String chatId;
}
