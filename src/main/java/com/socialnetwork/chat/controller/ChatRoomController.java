package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.model.request.ChatRoomDeleteRequest;
import com.socialnetwork.chat.model.request.chatRoomCreateRequest;
import com.socialnetwork.chat.model.response.ChatRoomInfoRequest;
import com.socialnetwork.chat.model.response.ChatRoomMessageRequest;
import com.socialnetwork.chat.model.response.ChatRoomResponse;
import com.socialnetwork.chat.model.response.MessageRequest;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.util.CustomPageable;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Chat API", description = "Rest chat methods")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Validated
    @GetMapping
    @Timed(value = "getChatRoomById.time")
    @Counted(value = "getChatRoomById.count")
    @Operation(summary = "Get chatRoom by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ChatRoomMessageRequest getChatRoomById(
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
    public Page<ChatRoomMessageRequest> findChatRoomsMessage(
        @CurrentUser UserSecurity userSecurity,
        CustomPageable pageable
    ) {
        return chatRoomService.findChatRoomsMessageByUserId(userSecurity.getUserId(), pageable.toPageable());
    }

    @PostMapping("/get-chat")
    @Timed(value = "getChatRoomByUsersOrElseCreate.time")
    @Counted(value = "getChatRoomByUsersOrElseCreate.count")
    @Operation(summary = "Get chatRooms by user id or else create", security = @SecurityRequirement(name = "bearerAuth"))
    public ChatRoomInfoRequest getChatRoomByUsersOrElseCreate(
        @Valid
        @RequestBody chatRoomCreateRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getChatRoomByUsersOrElseCreate(dto, userSecurity.getUserId());
    }

    @PostMapping("/get-system-chat")
    @Timed(value = "getSystemChatRoomByUserOrElseCreate.time")
    @Counted(value = "getSystemChatRoomByUserOrElseCreate.count")
    @Operation(summary = "Get system chatRooms by user id or else create")
    public ChatRoomResponse getSystemChatRoomByUserOrElseCreate(
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getSystemChatRoomByUserOrElseCreate(userSecurity.getUserId());
    }

    @PostMapping
    @Timed(value = "createChatRoom.time")
    @Counted(value = "createChatRoom.count")
    @Operation(summary = "Create chatRoom", security = @SecurityRequirement(name = "bearerAuth"))
    public ChatRoomResponse createChatRoom(
        @Valid
        @RequestBody chatRoomCreateRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.createChatRoom(dto, userSecurity.getUserId());
    }

    @DeleteMapping
    @Timed(value = "deleteChatRoom.time")
    @Counted(value = "deleteChatRoom.count")
    @Operation(summary = "Delete chatRoom", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean deleteChatRoom(
        @Valid
        @NotNull(message = "Id of chat should be not null")
        @RequestBody ChatRoomDeleteRequest dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.deleteChatRoom(dto, userSecurity.getUserId());
    }

    @Validated
    @Timed(value = "findAllMessageByChatRoomId.time")
    @Counted(value = "findAllMessageByChatRoomId.count")
    @GetMapping("/all-messages")
    @Operation(summary = "Find all message by chat root", security = @SecurityRequirement(name = "bearerAuth"))
    public Page<MessageRequest> findAllMessageByChatRoomId(
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
