package com.socialnetwork.chat.service;

import com.socialnetwork.chat.model.request.*;
import com.socialnetwork.chat.model.response.ChatRoomInfoRequest;
import com.socialnetwork.chat.model.response.ChatRoomMessageRequest;
import com.socialnetwork.chat.model.response.ChatRoomResponse;
import com.socialnetwork.chat.model.response.MessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {

    ChatRoomMessageRequest getChatRoomById(String userId, String chatId);

    ChatRoomInfoRequest getChatRoomByUsersOrElseCreate(chatRoomCreateRequest dto, String currentUserId);

    ChatRoomResponse getSystemChatRoomByUserOrElseCreate(String userId);

    Integer getAmountOfAllNotReadMessages(String userId);

    Page<MessageRequest> findMessagesByChatId(String userId, String chatId, Pageable pageable);

    Page<ChatRoomMessageRequest> findChatRoomsMessageByUserId(String userId, Pageable pageable);

    ChatRoomResponse createChatRoom(chatRoomCreateRequest dto, String currentUserId);

    boolean deleteChatRoom(ChatRoomDeleteRequest dto, String currentUserId);

    MessageRequest updateMessage(MessageUpdateRequest dto, String currentUserId);

    MessageRequest sendMessage(MessageCreateRequest dto, String currentUserId);

    MessageRequest deleteMessage(MessageDeleteRequest dto, String currentUserId);

    MessageRequest toggleLikeMessage(MessageLikeRequest dto, String currentUserId);

    MessageRequest readMessage(MessageReadRequest dto, String currentUserId);
}
