package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.util.CustomPageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(description = "Get chatRoom by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
    })
    public ChatRoomMessageDto getChatRoomById(
        @NotNull(message = "chat id should not be null")
        @RequestParam String chatId,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getChatRoomById(userSecurity.getUserId(), chatId);
    }

    @GetMapping("/find-chats")
    @Operation(description = "Find chatRooms with message by user id", security = @SecurityRequirement(name = "bearerAuth"))
    public Page<ChatRoomMessageDto> findChatRoomsMessage(
        @CurrentUser UserSecurity userSecurity,
        CustomPageable pageable
    ) {
        return chatRoomService.findChatRoomsMessageByUserId(userSecurity.getUserId(), pageable.toPageable());
    }

    @PostMapping("/get-chat")
    @Operation(description = "Get chatRooms by user id or else create", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1000", description = "user not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
    })
    public ChatRoomInfoDto getChatRoomByUsersOrElseCreate(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.getChatRoomByUsersOrElseCreate(dto);
    }

    @PostMapping("/get-system-chat")
    @Operation(description = "Get system chatRooms by user id or else create")
    public ChatRoomDto getSystemChatRoomByUserOrElseCreate(
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getSystemChatRoomByUserOrElseCreate(userSecurity.getUserId());
    }

    @PostMapping
    @Operation(description = "Create chatRoom", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1000", description = "user not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1003", description = "chat with these users already exits", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
    })
    public ChatRoomDto createChatRoom(
        @Valid
        @RequestBody ChatRoomCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.createChatRoom(dto);
    }

    @DeleteMapping
    @Operation(description = "Delete chatRoom", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
    })
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
    @GetMapping("/all-messages")
    @Operation(description = "Find all message by chat root", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        ))
    })
    public Page<MessageDto> findAllMessageByChatRoomId(
        @NotNull(message = "Id of chat should be not null")
        @RequestParam String chatId,
        CustomPageable pageable,
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.findMessagesByChatId(userSecurity.getUserId(), chatId, pageable.toPageable());
    }

    @GetMapping("/not-read-messages-amount")
    @Operation(description = "Amount of all not read message", security = @SecurityRequirement(name = "bearerAuth"))
    public Integer amountOfAllNotReadMessages(
        @CurrentUser UserSecurity userSecurity
    ) {
        return chatRoomService.getAmountOfAllNotReadMessages(userSecurity.getUserId());
    }
}
