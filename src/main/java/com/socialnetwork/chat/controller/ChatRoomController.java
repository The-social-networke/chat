package com.socialnetwork.chat.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import com.socialnetwork.chat.util.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Validated
@RestController
@Scope("session")
@RequestMapping("/chat")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomController {

    private final ChatRoomServiceImpl chatRoomService;

    private final UserId currentUser;

    @Validated
    @GetMapping
    public Optional<ChatRoom> findChatRoom(
        @NotNull(message = "chat id should not be null")
        @RequestParam String chatId
    ) {
        return chatRoomService.findChatRoomById(chatId);
    }

    @Validated
    @PostMapping
    public ChatRoom createChatRoom(
        @NotNull(message = "userId should not be null")
        @RequestParam String userId
    ) {
        return chatRoomService.createChatRoom(currentUser.getId(), userId);
    }

    @Validated
    @PostMapping("/get-chat")
    public ChatRoom findChatRoomByUsersOrElseCreate(
        @NotNull(message = "userId should not be null")
        @RequestParam String userId
    ) {
        return chatRoomService.findChatRoomByUsersOrElseCreate(currentUser.getId(), userId);
    }

    @Validated
    @PostMapping("/get-system-chat")
    public ChatRoom findSystemChatRoomByUserOrElseCreate() {
        return chatRoomService.findSystemChatRoomByUserOrElseCreate(currentUser.getId());
    }

    @Validated
    @DeleteMapping
    public boolean deleteChatRoom(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId
    ) {
        return chatRoomService.deleteChatRoom(chatId, currentUser.getId());
    }

    @Validated
    @GetMapping("/all-messages")
    public Page<Message> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        Pageable pageable
    ) {
        return chatRoomService.findMessagesByChatId(chatId, currentUser.getId(), pageable);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/sendMessage")
    public Message sendMessage(
        @Valid
        @RequestBody MessageCreateDto dto
    ) {
        //todo change it
        dto.setUserId(currentUser.getId());
        return chatRoomService.sendMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/deleteMessage")
    public Message deleteMessage(
        @Valid
        @RequestBody String messageId
    ) {
        return chatRoomService.deleteMessage(currentUser.getId(), messageId);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/updateMessage")
    public Message updateMessage(
        @Valid
        @RequestBody MessageUpdateDto dto
    ) {
        dto.setUserId(currentUser.getId());
        return chatRoomService.updateMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/likeMessage")
    public Message likeMessage(
        @Valid
        @RequestBody MessageLikeDto dto
    ) {
        dto.setUserId(currentUser.getId());
        return chatRoomService.toggleLikeMessage(dto);
    }

    @SendTo("/chat/messages")
    @MessageMapping("/chat/readMessage")
    public Message readMessage(
        @Valid
        @RequestBody MessageReadDto dto
    ) {
        return chatRoomService.readMessage(dto);
    }
}
