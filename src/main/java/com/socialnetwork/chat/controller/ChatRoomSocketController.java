package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomSocketController {

    private final ChatRoomServiceImpl chatRoomService;

    @PostMapping("/chat/sendMessage")
    public Message sendMessage(
        @Valid
        @RequestBody MessageCreateDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.sendMessage(dto);
    }

    @DeleteMapping("/chat/deleteMessage")
    public Message deleteMessage(
        @Valid
        @RequestBody MessageDeleteDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.deleteMessage(dto);
    }

    @PostMapping("/chat/updateMessage")
    public Message updateMessage(
        @Valid
        @RequestBody MessageUpdateDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.updateMessage(dto);
    }

    @PostMapping("/chat/likeMessage")
    public Message likeMessage(
        @Valid
        @RequestBody MessageLikeDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.toggleLikeMessage(dto);
    }

    @PostMapping("/chat/readMessage")
    public Message readMessage(
        @Valid
        @RequestBody MessageReadDto dto,
        Principal principal
    ) {
        dto.setCurrentUserId(principal.getName());
        return chatRoomService.readMessage(dto);
    }
}
