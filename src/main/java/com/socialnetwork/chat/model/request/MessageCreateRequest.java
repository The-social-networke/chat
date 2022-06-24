package com.socialnetwork.chat.model.request;

import com.socialnetwork.chat.model.enums.ForwardType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to create message")
public class MessageCreateRequest {

    @Schema(required = true,
        example = "some text",
        description = "text of message")
    @NotBlank(message = "Message should not be blank")
    private String text;

    @NotNull(message = "chat room id should not be null")
    @Schema(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Another user in chat room")
    private String chatRoomId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
