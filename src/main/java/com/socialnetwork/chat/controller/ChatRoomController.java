package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.ChatDeleteDto;
import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomController {

    private final ChatRoomServiceImpl chatRoomService;

    @Validated
    @GetMapping
    public Optional<ChatRoom> findChatRoom(
        @NotNull(message = "chat id should not be null")
        @RequestParam String chatId
    ) {
        return chatRoomService.findChatRoomById(chatId);
    }

    @PostMapping
    public ChatRoom createChatRoom(
        @Valid
        @RequestParam ChatRoomCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.createChatRoom(dto);
    }

    @Validated
    @PostMapping("/get-chat")
    public ChatRoom findChatRoomByUsersOrElseCreate(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.findChatRoomByUsersOrElseCreate(dto);
    }

    @Validated
    @PostMapping("/get-system-chat")
    public ChatRoom findSystemChatRoomByUserOrElseCreate(
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.findSystemChatRoomByUserOrElseCreate(userSecurity.getUserId());
    }

    @Validated
    @DeleteMapping
    public boolean deleteChatRoom(
        @NotNull(message = "Id of chat should be not null")
        @RequestBody ChatDeleteDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.deleteChatRoom(dto);
    }

    @Validated
    @GetMapping("/all-messages")
    public Page<Message> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        Pageable pageable,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.findMessagesByChatId(chatId, userSecurity.getUserId(), pageable);
    }
}
