package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomSocketController {

    private final ChatRoomServiceImpl chatRoomService;

    @SendTo("/chat/messages")
    @MessageMapping("/chat/sendMessage")
    public Message sendMessage(
        @Valid
        @RequestBody MessageCreateDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.sendMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/deleteMessage")
    public Message deleteMessage(
        @Valid
        @RequestBody MessageDeleteDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.deleteMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/updateMessage")
    public Message updateMessage(
        @Valid
        @RequestBody MessageUpdateDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.updateMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/likeMessage")
    public Message likeMessage(
        @Valid
        @RequestBody MessageLikeDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.toggleLikeMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/readMessage")
    public Message readMessage(
        @Valid
        @RequestBody MessageReadDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.readMessage(dto);
    }
}
