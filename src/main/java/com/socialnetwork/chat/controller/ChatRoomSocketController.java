package com.socialnetwork.chat.controller;

import com.socialnetwork.chat.config.security.CurrentUser;
import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.service.ChatRoomService;
import io.micrometer.core.annotation.Timed;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Chat socket API", description = "Rest chat methods with socket notification")
public class ChatRoomSocketController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/sendMessage")
    @Timed(value = "greeting.time", description = "Time taken to return greeting")
    @Operation(description = "This method send notification for chat and for user", security = @SecurityRequirement(name = "bearerAuth"))
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
    public MessageDto sendMessage(
        @Valid
        @RequestBody MessageCreateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.sendMessage(dto);
    }

    @DeleteMapping("/chat/deleteMessage")
    @Operation(description = "This method send notification for chat and if it last message send for user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1006", description = "user cannot delete not own message", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        ))
    })
    public MessageDto deleteMessage(
        @Valid
        @RequestBody MessageDeleteDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.deleteMessage(dto);
    }

    @PostMapping("/chat/updateMessage")
    @Operation(description = "This method send notification for chat and if it last message send for user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1007", description = "user cannot update not own message", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        ))
    })
    public MessageDto updateMessage(
        @Valid
        @RequestBody MessageUpdateDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.updateMessage(dto);
    }

    @PostMapping("/chat/likeMessage")
    @Operation(description = "This method send notification for chat", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1004", description = "user cannot like his message", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        ))
    })
    public MessageDto likeMessage(
        @Valid
        @RequestBody MessageLikeDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.toggleLikeMessage(dto);
    }

    @PostMapping("/chat/readMessage")
    @Operation(description = "This method send notification for chat", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "1001", description = "chat not found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1002", description = "not member of chat", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        )),
        @ApiResponse(responseCode = "1005", description = "user cannot read his message", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)
        ))
    })
    public MessageDto readMessage(
        @Valid
        @RequestBody MessageReadDto dto,
        @CurrentUser UserSecurity userSecurity
    ) {
        dto.setCurrentUserId(userSecurity.getUserId());
        return chatRoomService.readMessage(dto);
    }
}
