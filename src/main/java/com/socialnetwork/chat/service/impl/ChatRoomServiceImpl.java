package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatNotFoundException;
import com.socialnetwork.chat.exception.ChatWithTheseUsersAlreadyExists;
import com.socialnetwork.chat.exception.DeniedAccessNotMemberOfChatException;
import com.socialnetwork.chat.mapper.ChatRoomMapper;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final MessageService messageService;

    private final ChatRoomMapper chatRoomMapper;

    private String systemUserId = "8a744b81-38fd-4fe1-a032-33836e7a0221";


    @Override
    public Optional<ChatRoom> findChatRoomById(String id) {
        log.info("Find chat room");

        return chatRoomRepository.findById(id);
    }

    @Override
    @Transactional
    public ChatRoom findChatRoomByUsersOrElseCreate(ChatRoomCreateDto dto) {
        log.info("Find chat room by users");

        var chat = chatRoomRepository.findChatRoomByUsers(dto.getUsers());
        if(chat.isEmpty()) {
            var entity = chatRoomMapper.toEntity(dto)
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
            return chatRoomRepository.save(entity);
        }
        return chat.get();
    }

    @Override
    @Transactional
    public ChatRoom findSystemChatRoomByUserOrElseCreate(String userId) {
        log.info("Find system chat room by users");

        var chat = chatRoomRepository.findChatRoomByUsers(Set.of(userId, systemUserId));
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
    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateDto dto) {
        log.info("Create chat room");
        if(chatRoomRepository.existsChatRoomByUsers(dto.getUsers())) {
            throw new ChatWithTheseUsersAlreadyExists();
        }

        var entity = chatRoomMapper.toEntity(dto)
            .toBuilder()
            .id(UUID.randomUUID().toString())
            .build();
        return chatRoomRepository.save(entity);
    }

    @Override
    @Transactional
    public boolean deleteChatRoom(String chatId, String userId) {
        log.info("Deleted chat room");

        var chatRoom = getChatRoomOrElseThrow(chatId);
        checkIfUserMemberOfChat(chatRoom, userId);

        chatRoomRepository.deleteById(chatId);

        return true;
    }

    @Override
    @Transactional
    public Message sendMessage(MessageCreateDto dto) {
        log.info("Send message");

        var chatRoom = getChatRoomOrElseThrow(dto.getChatRoomId());
        checkIfUserMemberOfChat(chatRoom, dto.getUserId());

        return messageService.sendMessage(dto);
    }

    @Override
    @Transactional
    public Message deleteMessage(MessageDeleteDto dto) {
        log.info("Delete message");

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatNotFoundException();
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getUserId());

        return messageService.deleteMessage(dto);
    }

    @Override
    @Transactional
    public Message updateMessage(MessageUpdateDto dto) {
        log.info("Update message");

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatNotFoundException();
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getUserId());

        return messageService.updateMessage(dto);
    }

    @Override
    @Transactional
    public Message toggleLikeMessage(MessageLikeDto dto) {
        log.info("Like message {}", dto.getIsLike());

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatNotFoundException();
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getUserId());

        return messageService.toggleLikeMessage(dto);
    }

    @Override
    @Transactional
    public Message readMessage(MessageReadDto dto) {
        log.info("Read message");

        var chatRoomOfMessage = chatRoomRepository.findChatRoomByMessageId(dto.getMessageId());
        if(chatRoomOfMessage.isEmpty()) {
            throw new ChatNotFoundException();
        }
        checkIfUserMemberOfChat(chatRoomOfMessage.get(), dto.getUserId());

        return messageService.readMessage(dto);
    }

    private void checkIfUserMemberOfChat(ChatRoom chatRoom, String userId) throws DeniedAccessNotMemberOfChatException {
        boolean isMemberOfChat = chatRoom.getUsers()
            .stream()
            .anyMatch(u -> u.equals(userId));
        if(!isMemberOfChat) {
            throw new DeniedAccessNotMemberOfChatException(userId);
        }
    }

    private ChatRoom getChatRoomOrElseThrow(String chatId) throws ChatNotFoundException {
        var chatRoom = chatRoomRepository.findById(chatId);
        return chatRoom.orElseThrow(ChatNotFoundException::new);
    }
}
