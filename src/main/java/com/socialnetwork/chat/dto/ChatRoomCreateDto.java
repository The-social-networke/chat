package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to create chatRoom")
public class ChatRoomCreateDto {

    @NotEmpty(message = "user should be present")
    @Schema(required = true,
        example = "cfdbefcb-012e-4901-97e1-c673335558d7",
        description = "Another user in chat room")
    private String userId;

    @JsonIgnore
    private String currentUserId;
}
