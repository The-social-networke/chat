package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.util.AuthModuleUtil;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final MessageService messageService;

    @Value( "${app.system-user-id}" )
    private String systemUserId;

    @Value("${app.auth.url}")
    private String url;

    private final SimpMessagingTemplate template;

    private final RestTemplate restTemplate;


    @Override
    public Optional<ChatRoom> findChatRoomById(String id) {
        log.info("Find chat room");
        //todo make test


        return chatRoomRepository.findById(id);
    }

    @Override
    @Transactional
    public ChatRoom getChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto) {
        log.info("Find chat room by users");

        var chat = chatRoomRepository.findChatRoomByUsers(dto.getCurrentUserId(), dto.getUserId());
        if(chat.isEmpty()) {
            checkIfUserExists(dto.getUserId());
            var entity = new ChatRoom()
                .toBuilder()
                .users(Set.of(dto.getCurrentUserId(), dto.getUserId()))
                .id(UUID.randomUUID().toString())
                .build();
            return chatRoomRepository.save(entity);
        }
        return chat.get();
    }

    @Override
    @Transactional
    public ChatRoom getSystemChatRoomByUserOrElseCreate(String userId) {
        log.info("Find system chat room by users");

        var chat = chatRoomRepository.findChatRoomByUsers(userId, systemUserId);
        if(chat.isEmpty()) {
            var newChatRoom = new ChatRoom()
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .users(Set.of(userId, systemUserId))
                .build();
            return chatRoomRepository.save(newChatRoom);
        }
        return chat.get();
    }

    @Override
    public Page<Message> findMessagesByChatId(String chatId, String userId, Pageable pageable) {
        log.info("Find chat room");

        var chatRoom = getChatRoomOrElseThrow(chatId);
        checkIfUserMemberOfChat(chatRoom, userId);

        return messageService.findMessagesByChatId(chatId, pageable);
    }

    @Override
    public Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable) {
        return chatRoomRepository.findChatRoomsMessageByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateDto dto) {
        log.info("Create chat room");

        if(chatRoomRepository.existsChatRoomByUsers(dto.getCurrentUserId(), dto.getUserId())) {
            throw new ChatException(ErrorCodeException.CHAT_WITH_THESE_USERS_ALREADY_EXISTS);
        }
        checkIfUserExists(dto.getUserId());
        var entity = new ChatRoom()
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

        var chatRoom = getChatRoomOrElseThrow(dto.getChatId());
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        chatRoomRepository.deleteById(dto.getChatId());

        return true;
    }

    @Override
    @Transactional
    public Message sendMessage(MessageCreateDto dto) {
        log.info("Send message");

        var chatRoom = getChatRoomOrElseThrow(dto.getChatRoomId());
        checkIfUserMemberOfChat(chatRoom, dto.getCurrentUserId());

        var savedMessage = messageService.sendMessage(dto);
        String anotherUserId = chatRoom.getUsers()
            .stream()
            .filter(u -> !u.equals(dto.getCurrentUserId()))
            .findFirst()
            .get();
        ChatRoomsMessageDto chatRoomsMessageDto = new ChatRoomsMessageDto()
            .toBuilder()
            .chatRoomId(dto.getChatRoomId())
            .userId(anotherUserId)
            .sentAt(savedMessage.getSentAt())
            .text(savedMessage.getText())
            .build();
        template.convertAndSend("/users/" + anotherUserId, chatRoomsMessageDto);
        return savedMessage;
    }

    @Override
    @Transactional
    public void deleteMessage(MessageDeleteDto dto) {
        log.info("Delete message");

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatException(ErrorCodeException.CHAT_NOT_FOUND);
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getCurrentUserId());

        messageService.deleteMessage(dto);
    }

    @Override
    @Transactional
    public Message updateMessage(MessageUpdateDto dto) {
        log.info("Update message");

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatException(ErrorCodeException.CHAT_NOT_FOUND);
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getCurrentUserId());

        return messageService.updateMessage(dto);
    }

    @Override
    @Transactional
    public Message toggleLikeMessage(MessageLikeDto dto) {
        log.info("Like message {}", dto.getIsLike());

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatException(ErrorCodeException.CHAT_NOT_FOUND);
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getCurrentUserId());

        return messageService.toggleLikeMessage(dto);
    }

    @Override
    @Transactional
    public Message readMessage(MessageReadDto dto) {
        log.info("Read message");

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatException(ErrorCodeException.CHAT_NOT_FOUND);
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getCurrentUserId());

        return messageService.readMessage(dto);
    }

    private void checkIfUserMemberOfChat(ChatRoom chatRoom, String userId) throws ChatException {
        boolean isMemberOfChat = chatRoom.getUsers()
            .stream()
            .anyMatch(u -> u.equals(userId));
        if(!isMemberOfChat) {
            throw new ChatException(ErrorCodeException.NOT_MEMBER_OF_CHAT);
        }
    }

    private void checkIfUserExists(String userId) throws ChatException {
        if(!AuthModuleUtil.existsUserById(userId, url, restTemplate)) {
            throw new ChatException(ErrorCodeException.USER_NOT_FOUND);
        }
    }

    private ChatRoom getChatRoomOrElseThrow(String chatId) throws ChatException {
        var chatRoom = chatRoomRepository.findById(chatId);
        return chatRoom.orElseThrow(() -> new ChatException(ErrorCodeException.CHAT_NOT_FOUND));
    }
}
