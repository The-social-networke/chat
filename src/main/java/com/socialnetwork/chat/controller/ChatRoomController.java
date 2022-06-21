package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.util.CustomPageable;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Authorization")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Chat API", description = "Rest chat methods")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Validated
    @GetMapping
    @Timed(value = "getChatRoomById.time")
    @Counted(value = "getChatRoomById.count")
    @Operation(summary = "Get chatRoom by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ChatRoomMessageDto getChatRoomById(
        @NotNull(message = "chat id should not be null")
        @RequestParam String chatId,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getChatRoomById(userSecurity.getUserId(), chatId);
    }

    @GetMapping("/find-chats")
    @Timed(value = "findChatRoomsMessage.time")
    @Counted(value = "findChatRoomsMessage.count")
    @Operation(summary = "Find chatRooms with message by user id", security = @SecurityRequirement(name = "bearerAuth"))
    public Page<ChatRoomMessageDto> findChatRoomsMessage(
        @CurrentUser UserSecurity userSecurity,
        CustomPageable pageable
    ) {
        return chatRoomService.findChatRoomsMessageByUserId(userSecurity.getUserId(), pageable.toPageable());
    }

    @PostMapping("/get-chat")
    @Timed(value = "getChatRoomByUsersOrElseCreate.time")
    @Counted(value = "getChatRoomByUsersOrElseCreate.count")
    @Operation(summary = "Get chatRooms by user id or else create", security = @SecurityRequirement(name = "bearerAuth"))
    public ChatRoomInfoDto getChatRoomByUsersOrElseCreate(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.getChatRoomByUsersOrElseCreate(dto);
    }

    @PostMapping("/get-system-chat")
    @Timed(value = "getSystemChatRoomByUserOrElseCreate.time")
    @Counted(value = "getSystemChatRoomByUserOrElseCreate.count")
    @Operation(summary = "Get system chatRooms by user id or else create")
    public ChatRoomDto getSystemChatRoomByUserOrElseCreate(
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getSystemChatRoomByUserOrElseCreate(userSecurity.getUserId());
    }

    @PostMapping
    @Timed(value = "createChatRoom.time")
    @Counted(value = "createChatRoom.count")
    @Operation(summary = "Create chatRoom", security = @SecurityRequirement(name = "bearerAuth"))
    public ChatRoomDto createChatRoom(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.createChatRoom(dto);
    }

    @DeleteMapping
    @Timed(value = "deleteChatRoom.time")
    @Counted(value = "deleteChatRoom.count")
    @Operation(summary = "Delete chatRoom", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean deleteChatRoom(
        @Valid
        @NotNull(message = "Id of chat should be not null")
        @RequestBody ChatRoomDeleteDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.deleteChatRoom(dto);
    }

    @Validated
    @Timed(value = "findAllMessageByChatRoomId.time")
    @Counted(value = "findAllMessageByChatRoomId.count")
    @GetMapping("/all-messages")
    @Operation(summary = "Find all message by chat root", security = @SecurityRequirement(name = "bearerAuth"))
    public Page<MessageDto> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        CustomPageable pageable,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.findMessagesByChatId(userSecurity.getUserId(), chatId, pageable.toPageable());
    }

    @GetMapping("/not-read-messages-amount")
    @Timed(value = "findAllMessageByChatRoomId.time")
    @Counted(value = "findAllMessageByChatRoomId.count")
    @Operation(summary = "Amount of all not read message", security = @SecurityRequirement(name = "bearerAuth"))
    public Integer amountOfAllNotReadMessages(
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getAmountOfAllNotReadMessages(userSecurity.getUserId());
    }
}
