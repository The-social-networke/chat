package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.ChatRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "Chat socket API", description = "Rest chat methods with socket notification")
public class ChatRoomSocketController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/sendMessage")
    @ApiOperation(value = "Send message", notes = "This method send notification for chat and for user")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
    })
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
    @ApiOperation(value = "Delete message", notes = "This method send notification for chat and if it last message send for user")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
        @ApiResponse(code = 1006, message = "user cannot delete not own message", response = ErrorDto.class),
    })
    public Message deleteMessage(
        @Valid
        @RequestBody MessageDeleteDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.deleteMessage(dto);
    }

    @PostMapping("/chat/updateMessage")
    @ApiOperation(value = "Update message", notes = "This method send notification for chat and if it last message send for user")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
        @ApiResponse(code = 1007, message = "user cannot update not own message", response = ErrorDto.class),
    })
    public Message updateMessage(
        @Valid
        @RequestBody MessageUpdateDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.updateMessage(dto);
    }

    @PostMapping("/chat/likeMessage")
    @ApiOperation(value = "Update message", notes = "This method send notification for chat")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
        @ApiResponse(code = 1004, message = "user cannot like his message", response = ErrorDto.class),
    })
    public Message likeMessage(
        @Valid
        @RequestBody MessageLikeDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.toggleLikeMessage(dto);
    }

    @PostMapping("/chat/readMessage")
    @ApiOperation(value = "Update message", notes = "This method send notification for chat")
    @ApiResponses(value = {
        @ApiResponse(code = 1001, message = "chat not found", response = ErrorDto.class),
        @ApiResponse(code = 1002, message = "not member of chat", response = ErrorDto.class),
        @ApiResponse(code = 1005, message = "user cannot read his message", response = ErrorDto.class),
    })
    public Message readMessage(
        @Valid
        @RequestBody MessageReadDto dto,
        @ApiIgnore
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.readMessage(dto);
    }
}
