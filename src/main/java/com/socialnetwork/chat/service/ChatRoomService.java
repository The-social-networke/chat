package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomService {

    Optional<ChatRoom> findChatRoomById(String id);

    ChatRoom getChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto);

    ChatRoom getSystemChatRoomByUserOrElseCreate(String userId);

    Page<Message> findMessagesByChatId(String chatId, String userId, Pageable pageable);

    Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable);

    ChatRoom createChatRoom(ChatRoomCreateDto dto);

    boolean deleteChatRoom(ChatRoomDeleteDto dto);

    Message updateMessage(MessageUpdateDto dto);

    Message sendMessage(MessageCreateDto dto);

    void deleteMessage(MessageDeleteDto dto);

    Message toggleLikeMessage(MessageLikeDto dto);

    Message readMessage(MessageReadDto dto);
}
