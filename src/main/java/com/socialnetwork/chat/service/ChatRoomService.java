package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {

    ChatRoomMessageDto getChatRoomById(String userId, String chatId);

    ChatRoomInfoDto getChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto);

    ChatRoomDto getSystemChatRoomByUserOrElseCreate(String userId);

    Integer getAmountOfAllNotReadMessages(String userId);

    Page<MessageDto> findMessagesByChatId(String userId, String chatId, Pageable pageable);

    Page<ChatRoomMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable);

    ChatRoomDto createChatRoom(ChatRoomCreateDto dto);

    boolean deleteChatRoom(ChatRoomDeleteDto dto);

    MessageDto updateMessage(MessageUpdateDto dto);

    MessageDto sendMessage(MessageCreateDto dto);

    MessageDto deleteMessage(MessageDeleteDto dto);

    MessageDto toggleLikeMessage(MessageLikeDto dto);

    MessageDto readMessage(MessageReadDto dto);
}
