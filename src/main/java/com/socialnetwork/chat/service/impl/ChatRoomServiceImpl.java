package com.socialnetwork.chat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.util.AuthModuleUtil;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import com.socialnetwork.chat.util.enums.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomServiceImpl implements ChatRoomService {

    private static final String USER_SOCKET_NOTIFICATION = "/users/";

    private static final String CHAT_SOCKET_NOTIFICATION = "/chat/messages/";

    private final MessageRepository messageRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final MessageService messageService;

    @Value( "${app.system-user-id}" )
    private String systemUserId;

    @Value("${app.auth.url}")
    private String url;

    @Value("${app.auth.endpoint.get-info-by-user-id}")
    private String endpointGetInfoByUserId;

    private final SimpMessagingTemplate template;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;


    @Override
    public ChatRoom getChatRoomById(String userId, String chatId) {
        log.info("Find chat room with userId = {} and chatId = {}", userId, chatId);

        ChatRoom chat = chatRoomRepository.findById(chatId)
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chat, userId);
        return chat;
    }

    @Override
    @Transactional
    public ChatRoomInfoDto getChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto) {
        log.info("getChatRoomByUsersOrElseCreate by users with currentUserId = {}, and userId = {}", dto.getCurrentUserId(), dto.getUserId());

        Optional<ChatRoom> chat = chatRoomRepository.findChatRoomByUsers(dto.getCurrentUserId(), dto.getUserId());
        if(chat.isEmpty()) {
            checkIfUserExists(dto.getUserId());
            ChatRoom entity = new ChatRoom()
                .toBuilder()
                .users(Set.of(dto.getCurrentUserId(), dto.getUserId()))
                .id(UUID.randomUUID().toString())
                .build();
            ChatRoom savedChatRoom = chatRoomRepository.save(entity);
            return new ChatRoomInfoDto()
                .toBuilder()
                .id(savedChatRoom.getId())
                .users(savedChatRoom.getUsers())
                .createdAt(savedChatRoom.getCreatedAt())
                .amountOfNotReadMessages(0)
                .build();
        }
        ChatRoom foundChatRoom = chat.get();
        return new ChatRoomInfoDto()
            .toBuilder()
            .id(foundChatRoom.getId())
            .users(foundChatRoom.getUsers())
            .createdAt(foundChatRoom.getCreatedAt())
            .amountOfNotReadMessages(chatRoomRepository.getAmountOfNotReadMessages(foundChatRoom.getId()))
            .build();
    }

    @Override
    @Transactional
    public ChatRoom getSystemChatRoomByUserOrElseCreate(String userId) {
        log.info("Find system chat room by users with userId = {}", userId);

        Optional<ChatRoom> chat = chatRoomRepository.findChatRoomByUsers(userId, systemUserId);
        if(chat.isEmpty()) {
            ChatRoom newChatRoom = new ChatRoom()
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .users(Set.of(userId, systemUserId))
                .build();
            return chatRoomRepository.save(newChatRoom);
        }
        return chat.get();
    }

    @Override
    public Page<Message> findMessagesByChatId(String userId, String chatId, Pageable pageable) {
        log.info("Find chat room");

        ChatRoom chatRoom = getChatRoomOrElseThrow(chatId);
        checkIfUserMemberOfChat(chatRoom, userId);

        return messageService.findMessagesByChatId(chatId, pageable);
    }

    @Override
    public Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable) {
        log.info("Find chat room message by user id");

        var result = chatRoomRepository.findChatRoomsMessageByUserId(userId, pageable);
        result = result.map(u -> u.toBuilder()
            .userInfo(getUserInfoByUserId(u.getAnotherUserId()))
            .build());

        return result;
    }

    @Override
    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateDto dto) {
        log.info("Create chat room");

        if(chatRoomRepository.existsChatRoomByUsers(dto.getCurrentUserId(), dto.getUserId())) {
            throw new ChatException(ErrorCodeException.CHAT_WITH_THESE_USERS_ALREADY_EXISTS);
        }
        checkIfUserExists(dto.getUserId());

        ChatRoom entity = new ChatRoom()
            .toBuilder()
            .users(Set.of(dto.getCurrentUserId(), dto.getUserId()))
            .id(UUID.randomUUID().toString())
            .build();
        return chatRoomRepository.save(entity);
    }

    @Override
    @Transactional
    public boolean deleteChatRoom(ChatRoomDeleteDto dto) {
        log.info("Deleted chat room");

        ChatRoom chatRoom = getChatRoomOrElseThrow(dto.getChatId());
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        chatRoomRepository.deleteById(dto.getChatId());

        return true;
    }

    @Override
    @Transactional
    public Message sendMessage(MessageCreateDto dto) {
        log.info("Send message");

        ChatRoom chatRoom = getChatRoomOrElseThrow(dto.getChatRoomId());
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message savedMessage = messageService.sendMessage(dto);

        String anotherUserId = getAnotherUserIdFromChat(chatRoom, dto.getCurrentUserId());
        template.convertAndSend(USER_SOCKET_NOTIFICATION + anotherUserId, convertToChatRoomMessageStatusDto(chatRoom.getId(), savedMessage));
        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + dto.getChatRoomId(), savedMessage);

        return savedMessage;
    }

    @Override
    @Transactional
    public Message deleteMessage(MessageDeleteDto dto) {
        log.info("Delete message");

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        boolean isLastMessage = chatRoomRepository.isLastMessageInChatRoom(chatRoom.getId(), dto.getMessageId());

        Message deletedMessage = messageService.deleteMessage(dto);

        if(isLastMessage) {
            var lastMessage = messageRepository.findLastMessageInChat(chatRoom.getId())
                .map(m -> m.toBuilder().messageStatus(MessageStatus.DELETED).build());
            var messageStatusDto = convertToChatRoomMessageStatusDto(chatRoom.getId(), lastMessage.orElse(null));
            template.convertAndSend(USER_SOCKET_NOTIFICATION + getAnotherUserIdFromChat(chatRoom, dto.getCurrentUserId()), messageStatusDto);
        }
        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(), deletedMessage);

        return deletedMessage;
    }

    @Override
    @Transactional
    public Message updateMessage(MessageUpdateDto dto) {
        log.info("Update message");

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message updatedMessage = messageService.updateMessage(dto);

        if(chatRoomRepository.isLastMessageInChatRoom(chatRoom.getId(), dto.getMessageId())) {
            var messageStatusDto = convertToChatRoomMessageStatusDto(chatRoom.getId(), updatedMessage);
            template.convertAndSend(USER_SOCKET_NOTIFICATION + getAnotherUserIdFromChat(chatRoom, dto.getCurrentUserId()), messageStatusDto);
        }

        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(), updatedMessage);
        return  updatedMessage;
    }

    @Override
    @Transactional
    public Message toggleLikeMessage(MessageLikeDto dto) {
        log.info("Like message {}", dto.getIsLike());

        var chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message changedMessage = messageService.toggleLikeMessage(dto);

        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(), changedMessage);

        return changedMessage;
    }

    @Override
    @Transactional
    public Message readMessage(MessageReadDto dto) {
        log.info("Read message");

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message changedMessage = messageService.readMessage(dto);

        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(), changedMessage);

        return changedMessage;
    }


    private static void checkIfUserMemberOfChat(ChatRoom chatRoom, String userId) throws ChatException {
        boolean isMemberOfChat = chatRoom.getUsers()
            .stream()
            .anyMatch(u -> u.equals(userId));
        if(!isMemberOfChat) {
            throw new ChatException(ErrorCodeException.NOT_MEMBER_OF_CHAT);
        }
    }

    private void checkIfUserExists(String userId) throws ChatException {
        try {
            if (!AuthModuleUtil.existsUserById(userId, url, restTemplate)) {
                throw new ChatException(ErrorCodeException.USER_NOT_FOUND);
            }
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw new ChatException(ErrorCodeException.USER_NOT_FOUND);
        }
    }


    private ChatRoom getChatRoomOrElseThrow(String chatId) throws ChatException {
        return chatRoomRepository.findById(chatId)
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
    }

    private String getAnotherUserIdFromChat(ChatRoom chatRoom, String userId) throws ChatException {
        return chatRoom.getUsers()
            .stream()
            .filter(u -> !u.equals(userId))
            .findFirst()
            .orElseThrow();
    }

    private ChatRoomMessageStatusDto convertToChatRoomMessageStatusDto(String chatRoomId, Message message) {
        return new ChatRoomMessageStatusDto()
            .toBuilder()
            .chatRoomId(chatRoomId)
            .messageId(message == null ? null : message.getId())
            .text(message == null ? null : message.getText())
            .sentAt(message == null ? null : message.getSentAt())
            .userId(message == null ? null : message.getUserId())
            .messageStatus(message == null ? null :  message.getMessageStatus())
            .userInfo(message == null ? null : getUserInfoByUserId(message.getUserId()))
            .build();
    }

    private Object getUserInfoByUserId(String userid) {
        String userInfoString = restTemplate.getForObject(url + endpointGetInfoByUserId + userid, String.class);
        Object userInfo = null;
        if(userInfoString != null) {
            try {
                userInfo = objectMapper.readValue(userInfoString, Map.class);
            } catch (JsonProcessingException e) {
                log.error("some problem with parsing json to map", e);
            }
        }
        return userInfo;
    }
}
