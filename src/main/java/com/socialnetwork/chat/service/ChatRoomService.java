package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomService {

    Optional<ChatRoom> findChatRoomById(String id);

    ChatRoom findChatRoomByUsersOrElseCreate(String currentUserId, String anotherUser);

    ChatRoom findSystemChatRoomByUserOrElseCreate(String userId);

    Page<Message> findMessagesByChatId(String chatId, String userId, Pageable pageable);

    ChatRoom createChatRoom(String currentUserId, String anotherUser);

    boolean deleteChatRoom(String chatId, String userId);

    Message updateMessage(MessageUpdateDto dto);

    Message sendMessage(MessageCreateDto dto);

    Message deleteMessage(String userId, String messageId);

    Message toggleLikeMessage(MessageLikeDto dto);

    Message readMessage(MessageReadDto dto);
}
