package com.socialnetwork.chat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.ChatRoomUser;
import com.socialnetwork.chat.entity.ChatRoomUserPk;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.mapper.ChatRoomMapper;
import com.socialnetwork.chat.mapper.MessageMapper;
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
    public ChatRoomMessageDto getChatRoomById(String userId, String chatId) {
        log.info("Find chat room with userId = {} and chatId = {}", userId, chatId);

        ChatRoom chat = chatRoomRepository.findById(chatId)
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chat, userId);

        return chatRoomRepository.getChatRoomMessageByUserIdAndChatId(userId, chatId)
            .toBuilder()
            .userInfo(getUserInfoByUserId(getAnotherUserIdFromChat(chat, userId)))
            .build();
    }

    @Override
    @Transactional
    public ChatRoomInfoDto getChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto) {
        log.info("getChatRoomByUsersOrElseCreate by users with currentUserId = {}, and userId = {}", dto.getCurrentUserId(), dto.getUserId());

        if(dto.getCurrentUserId().equals(dto.getUserId())) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_CREATE_CHAT_WITH_HIMSELF);
        }

        Optional<ChatRoom> chat = chatRoomRepository.findChatRoomByUsers(dto.getCurrentUserId(), dto.getUserId());
        if(chat.isEmpty()) {
            checkIfUserExists(dto.getUserId());
            ChatRoom entity = new ChatRoom()
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .build();

            entity.setUsers(
                Set.of(
                    new ChatRoomUser(new ChatRoomUserPk(dto.getUserId(), entity.getId()), entity)
                )
            );
            ChatRoom savedChatRoom = chatRoomRepository.save(entity);
            return ChatRoomMapper.toChatRoomInfoDto(savedChatRoom, 0);
        }
        ChatRoom foundChatRoom = chat.get();

        return ChatRoomMapper.toChatRoomInfoDto(foundChatRoom, chatRoomRepository.getAmountOfNotReadMessages(foundChatRoom.getId()));
    }

    @Override
    @Transactional
    public ChatRoomDto getSystemChatRoomByUserOrElseCreate(String userId) {
        log.info("Find system chat room by users with userId = {}", userId);

        Optional<ChatRoom> chat = chatRoomRepository.findChatRoomByUsers(userId, systemUserId);
        if(chat.isEmpty()) {
            String chatId = UUID.randomUUID().toString();
            ChatRoom newChatRoom = new ChatRoom();
            newChatRoom.setId(chatId);
            newChatRoom.setUsers(Set.of(
                    new ChatRoomUser(new ChatRoomUserPk(userId, chatId), newChatRoom),
                    new ChatRoomUser(new ChatRoomUserPk(systemUserId, chatId), newChatRoom)
            ));
            return ChatRoomMapper.toChatRoomDto(chatRoomRepository.save(newChatRoom));
        }
        return ChatRoomMapper.toChatRoomDto(chat.get());
    }

    @Override
    public Integer getAmountOfAllNotReadMessages(String userId) {
        return chatRoomRepository.getAmountOfAllNotReadMessages(userId);
    }

    @Override
    public Page<MessageDto> findMessagesByChatId(String userId, String chatId, Pageable pageable) {
        log.info("Find chat room");

        ChatRoom chatRoom = getChatRoomOrElseThrow(chatId);
        checkIfUserMemberOfChat(chatRoom, userId);

        return messageService.findMessagesByChatId(chatId, pageable)
            .map(MessageMapper::toMessageDto);
    }

    @Override
    public Page<ChatRoomMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable) {
        log.info("Find chat room message by user id");

        var result = chatRoomRepository.findChatRoomsMessageByUserId(userId, pageable);
        result = result.map(u -> u.toBuilder()
            .userInfo(getUserInfoByUserId(u.getAnotherUserId()))
            .build());

        return result;
    }

    @Override
    @Transactional
    public ChatRoomDto createChatRoom(ChatRoomCreateDto dto) {
        log.info("Create chat room");

        if(dto.getCurrentUserId().equals(dto.getUserId())) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_CREATE_CHAT_WITH_HIMSELF);
        }
        if(chatRoomRepository.existsChatRoomByUsers(dto.getCurrentUserId(), dto.getUserId())) {
            throw new ChatException(ErrorCodeException.CHAT_WITH_THESE_USERS_ALREADY_EXISTS);
        }
        checkIfUserExists(dto.getUserId());

        String chatId = UUID.randomUUID().toString();
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setId(chatId);
        newChatRoom.setUsers(Set.of(
            new ChatRoomUser(new ChatRoomUserPk(dto.getCurrentUserId(), chatId), newChatRoom),
            new ChatRoomUser(new ChatRoomUserPk(dto.getUserId(), chatId), newChatRoom)
        ));
        return ChatRoomMapper.toChatRoomDto(chatRoomRepository.save(newChatRoom));
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
    public MessageDto sendMessage(MessageCreateDto dto) {
        log.info("Send message");

        ChatRoom chatRoom = getChatRoomOrElseThrow(dto.getChatRoomId());
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message savedMessage = messageService.sendMessage(dto);

        String anotherUserId = getAnotherUserIdFromChat(chatRoom, dto.getCurrentUserId());
        template.convertAndSend(USER_SOCKET_NOTIFICATION + anotherUserId, convertToChatRoomMessageStatusDto(chatRoom.getId(), savedMessage));
        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + dto.getChatRoomId(), MessageMapper.toMessageDto(savedMessage));

        return MessageMapper.toMessageDto(savedMessage);
    }

    @Override
    @Transactional
    public MessageDto deleteMessage(MessageDeleteDto dto) {
        log.info("Delete message");

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        boolean isLastMessage = chatRoomRepository.isLastMessageInChatRoom(chatRoom.getId(), dto.getMessageId());

        Message deletedMessage = messageService.deleteMessage(dto);

        if(isLastMessage) {
            var lastMessage = messageRepository.findFirstByChatRoomIdOrderBySentAtDesc(chatRoom.getId())
                .orElse(new Message()
                    .toBuilder()
                    .chatRoom(chatRoom)
                    .userId(dto.getCurrentUserId())
                    .build())
                .toBuilder()
                .messageStatus(MessageStatus.DELETED)
                .build();
            var messageStatusDto = convertToChatRoomMessageStatusDto(chatRoom.getId(), lastMessage);
            template.convertAndSend(USER_SOCKET_NOTIFICATION + getAnotherUserIdFromChat(chatRoom, dto.getCurrentUserId()), messageStatusDto);
        }
        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(),  MessageMapper.toMessageDto(deletedMessage));

        return MessageMapper.toMessageDto(deletedMessage);
    }

    @Override
    @Transactional
    public MessageDto updateMessage(MessageUpdateDto dto) {
        log.info("Update message");

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message updatedMessage = messageService.updateMessage(dto);

        if(chatRoomRepository.isLastMessageInChatRoom(chatRoom.getId(), dto.getMessageId())) {
            var messageStatusDto = convertToChatRoomMessageStatusDto(chatRoom.getId(), updatedMessage);
            template.convertAndSend(USER_SOCKET_NOTIFICATION + getAnotherUserIdFromChat(chatRoom, dto.getCurrentUserId()), messageStatusDto);
        }

        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(),  MessageMapper.toMessageDto(updatedMessage));
        return  MessageMapper.toMessageDto(updatedMessage);
    }

    @Override
    @Transactional
    public MessageDto toggleLikeMessage(MessageLikeDto dto) {
        log.info("Like message {}", dto.getIsLike());

        var chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message changedMessage = messageService.toggleLikeMessage(dto);

        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(),  MessageMapper.toMessageDto(changedMessage));

        return MessageMapper.toMessageDto(changedMessage);
    }

    @Override
    @Transactional
    public MessageDto readMessage(MessageReadDto dto) {
        log.info("Read message");

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId())
            .orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        Message changedMessage = messageService.readMessage(dto);

        template.convertAndSend(CHAT_SOCKET_NOTIFICATION + chatRoom.getId(),  MessageMapper.toMessageDto(changedMessage));

        return MessageMapper.toMessageDto(changedMessage);
    }


    private static void checkIfUserMemberOfChat(ChatRoom chatRoom, String userId) throws ChatException {
        boolean isMemberOfChat = chatRoom.getUsers()
            .stream()
            .anyMatch(u -> u.getChatRoomUserPk().getUserId().equals(userId));
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
            .map(u -> u.getChatRoomUserPk().getUserId())
            .filter(u -> !u.equals(userId))
            .findFirst()
            .orElseThrow();
    }

    private ChatRoomMessageStatusDto convertToChatRoomMessageStatusDto(String chatRoomId, Message message) {
        return new ChatRoomMessageStatusDto()
            .toBuilder()
            .chatRoomId(chatRoomId)
            .messageId(message.getId())
            .text(message.getText())
            .sentAt(message.getSentAt())
            .userId(message.getUserId())
            .messageStatus(message.getMessageStatus())
            .userInfo(getUserInfoByUserId(message.getUserId()))
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
