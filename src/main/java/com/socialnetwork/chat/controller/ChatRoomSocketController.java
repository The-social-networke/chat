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

    @SendTo("/chat/messages/{room}")
    @MessageMapping("/chat/sendMessage/{room}")
    public Message sendMessage(
        @Valid
        @RequestBody MessageCreateDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.sendMessage(dto);
    }

    @SendTo("/chat/messages/{room}")
    @MessageMapping("/chat/deleteMessage/{room}")
    public void deleteMessage(
        @Valid
        @RequestBody MessageDeleteDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        chatRoomService.deleteMessage(dto);
    }

    @SendTo("/chat/messages/{room}")
    @MessageMapping("/chat/updateMessage/{room}")
    public Message updateMessage(
        @Valid
        @RequestBody MessageUpdateDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.updateMessage(dto);
    }

    @SendTo("/chat/messages/{room}")
    @MessageMapping("/chat/likeMessage/{room}")
    public Message likeMessage(
        @Valid
        @RequestBody MessageLikeDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.toggleLikeMessage(dto);
    }

    @SendTo("/chat/messages/{room}")
    @MessageMapping("/chat/readMessage/{room}")
    public Message readMessage(
        @Valid
        @RequestBody MessageReadDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.readMessage(dto);
    }
}
