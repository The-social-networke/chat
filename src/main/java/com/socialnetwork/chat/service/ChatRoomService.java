package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomService {

    ChatRoom createChatRoom(ChatRoomCreateDto chatRoomDto);

    boolean deleteChatRoom(String chatId, String userId);

    Optional<ChatRoom> findChatRoomById(String id);

    Page<Message> findMessagesByChatId(String chatId, String userId, Pageable pageable);

    Message sendMessage(MessageCreateDto dto);

    Message deleteMessage(MessageDeleteDto dto);

    Message toggleLikeMessage(MessageLikeDto dto);

    Message readMessage(MessageReadDto dto);
}
