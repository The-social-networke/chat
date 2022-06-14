package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.util.CustomPageable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Authorization")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "Chat API", description = "Rest chat methods")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Validated
    @GetMapping
    @ApiOperation(value = "Get chatRoom by id")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
    })
    public ChatRoomMessageDto getChatRoomById(
        @NotNull(message = "chat id should not be null")
        @RequestParam String chatId,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getChatRoomById(userSecurity.getUserId(), chatId);
    }

    @GetMapping("/find-chats")
    @ApiOperation(value = "Find chatRooms with message by user id")
    public Page<ChatRoomMessageDto> findChatRoomsMessage(
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity,
        CustomPageable pageable
    ) {
        return chatRoomService.findChatRoomsMessageByUserId(userSecurity.getUserId(), pageable.toPageable());
    }

    @PostMapping("/get-chat")
    @ApiOperation(value = "Get chatRooms by user id or else create")
    @ApiResponses(value = {
        @ApiResponse(code = 1000, message = "user not found", response = ErrorDto.class)
    })
    public ChatRoomInfoDto getChatRoomByUsersOrElseCreate(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.getChatRoomByUsersOrElseCreate(dto);
    }

    @PostMapping("/get-system-chat")
    @ApiOperation(value = "Get system chatRooms by user id or else create")
    public ChatRoomDto getSystemChatRoomByUserOrElseCreate(
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getSystemChatRoomByUserOrElseCreate(userSecurity.getUserId());
    }

    @PostMapping
    @ApiOperation(value = "Create chatRoom")
    @ApiResponses(value = {
        @ApiResponse(code = 1000, message = "user not found", response = ErrorDto.class),
        @ApiResponse(code = 1003, message = "chat with these users already exits", response = ErrorDto.class),
    })
    public ChatRoomDto createChatRoom(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.createChatRoom(dto);
    }

    @DeleteMapping
    @ApiOperation(value = "Delete chatRoom")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
    })
    public boolean deleteChatRoom(
        @Valid
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
    @ApiOperation(value = "Find all message by chat root")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class)
    })
    public Page<MessageDto> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        CustomPageable pageable,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.findMessagesByChatId(userSecurity.getUserId(), chatId, pageable.toPageable());
    }

    @GetMapping("/not-read-messages-amount")
    @ApiOperation(value = "Amount of all not read message")
    public Integer amountOfAllNotReadMessages(
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getAmountOfAllNotReadMessages(userSecurity.getUserId());
    }
}
