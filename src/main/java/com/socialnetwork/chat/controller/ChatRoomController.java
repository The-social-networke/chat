package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
        @RequestBody ChatRoomCreateDto dto
    ) {
        return chatRoomService.createChatRoom(dto);
    }

    @Validated
    @DeleteMapping
    public boolean deleteChatRoom(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String userId
    ) {
        return chatRoomService.deleteChatRoom(chatId, userId);
    }

    @Validated
    @GetMapping("/all-messages")
    public Page<Message> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        @NotNull(message = "Id of user should be not null")
        @RequestParam String userId,
        Pageable pageable
    ) {
        return chatRoomService.findMessagesByChatId(chatId, userId, pageable);
    }

    @MessageMapping("/chat/sendMessage")
    @SendTo("/chat/messages")
    public Message sendMessage(
        @Valid
        @RequestBody MessageCreateDto dto
    ) {
        return chatRoomService.sendMessage(dto);
    }
}
