package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.dto.ChatRoomDeleteDto;
import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "Chat API", description = "Allows you to interact with chat")
public class ChatRoomController {

    private final ChatRoomServiceImpl chatRoomService;

    @Validated
    @GetMapping
    @ApiOperation(value = "Find chatRoom by id")
    public Optional<ChatRoom> findChatRoomById(
        @NotNull(message = "chat id should not be null")
        @RequestParam String chatId
    ) {
        return chatRoomService.findChatRoomById(chatId);
    }

    @GetMapping("/find-chats")
    @ApiOperation(value = "Find chatRooms with message by user id")
    public Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity,
        Pageable pageable
    ) {
        return chatRoomService.findChatRoomsMessageByUserId(userSecurity.getUserId(), pageable);
    }

    @Validated
    @PostMapping("/get-chat")
    @ApiOperation(value = "Get chatRooms by user id or else create")
    public ChatRoom getChatRoomByUsersOrElseCreate(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.getChatRoomByUsersOrElseCreate(dto);
    }

    @Validated
    @PostMapping("/get-system-chat")
    @ApiOperation(value = "Get system chatRooms by user id or else create")
    public ChatRoom getSystemChatRoomByUserOrElseCreate(
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getSystemChatRoomByUserOrElseCreate(userSecurity.getUserId());
    }

    @PostMapping
    @ApiOperation(value = "Create chatRoom")
    public ChatRoom createChatRoom(
        @Valid
        @RequestParam ChatRoomCreateDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.createChatRoom(dto);
    }

    @Validated
    @DeleteMapping
    @ApiOperation(value = "Delete chatRoom")
    public boolean deleteChatRoom(
        @NotNull(message = "Id of chat should be not null")
        @RequestBody ChatRoomDeleteDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.deleteChatRoom(dto);
    }

    @Validated
    @GetMapping("/all-messages")
    @ApiOperation(value = "Delete chatRoom")
    public Page<Message> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        Pageable pageable,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.findMessagesByChatId(chatId, userSecurity.getUserId(), pageable);
    }
}
