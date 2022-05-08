package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {

    ChatRoomMessageDto getChatRoomById(String userId, String chatId);

    ChatRoomInfoDto getChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto);

    ChatRoom getSystemChatRoomByUserOrElseCreate(String userId);

    Integer getAmountOfAllNotReadMessages(String userId);

    Page<Message> findMessagesByChatId(String userId, String chatId, Pageable pageable);

    Page<ChatRoomMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable);

    ChatRoom createChatRoom(ChatRoomCreateDto dto);

    boolean deleteChatRoom(ChatRoomDeleteDto dto);

    Message updateMessage(MessageUpdateDto dto);

    Message sendMessage(MessageCreateDto dto);

    Message deleteMessage(MessageDeleteDto dto);

    Message toggleLikeMessage(MessageLikeDto dto);

    Message readMessage(MessageReadDto dto);
}
