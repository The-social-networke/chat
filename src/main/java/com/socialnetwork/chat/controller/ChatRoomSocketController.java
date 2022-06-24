package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.model.request.*;
import com.socialnetwork.chat.model.response.MessageRequest;
import com.socialnetwork.chat.service.ChatRoomService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Chat socket API", description = "Rest chat methods with socket notification")
public class ChatRoomSocketController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/sendMessage")
    @Timed(value = "sendMessage.time")
    @Counted(value = "sendMessage.count")
    @Operation(summary = "send message", description = "This method send notification for chat and for user", security = @SecurityRequirement(name = "bearerAuth"))
    public MessageRequest sendMessage(
        @Valid
        @RequestBody MessageCreateRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.sendMessage(dto, userSecurity.getUserId());
    }

    @DeleteMapping("/chat/deleteMessage")
    @Timed(value = "deleteMessage.time")
    @Counted(value = "deleteMessage.count")
    @Operation(summary = "delete message", description = "This method send notification for chat and if it last message send for user", security = @SecurityRequirement(name = "bearerAuth"))
    public MessageRequest deleteMessage(
        @Valid
        @RequestBody MessageDeleteRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.deleteMessage(dto, userSecurity.getUserId());
    }

    @PostMapping("/chat/updateMessage")
    @Timed(value = "updateMessage.time")
    @Counted(value = "updateMessage.count")
    @Operation(summary = "update message", description = "This method send notification for chat and if it last message send for user", security = @SecurityRequirement(name = "bearerAuth"))
    public MessageRequest updateMessage(
        @Valid
        @RequestBody MessageUpdateRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.updateMessage(dto, userSecurity.getUserId());
    }

    @PostMapping("/chat/likeMessage")
    @Timed(value = "likeMessage.time")
    @Counted(value = "likeMessage.count")
    @Operation(summary = "like message", description = "This method send notification for chat", security = @SecurityRequirement(name = "bearerAuth"))
    public MessageRequest likeMessage(
        @Valid
        @RequestBody MessageLikeRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.toggleLikeMessage(dto, userSecurity.getUserId());
    }

    @PostMapping("/chat/readMessage")
    @Timed(value = "readMessage.time")
    @Counted(value = "readMessage.count")
    @Operation(summary = "read message", description = "This method send notification for chat", security = @SecurityRequirement(name = "bearerAuth"))
    public MessageRequest readMessage(
        @Valid
        @RequestBody MessageReadRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.readMessage(dto, userSecurity.getUserId());
    }
}
