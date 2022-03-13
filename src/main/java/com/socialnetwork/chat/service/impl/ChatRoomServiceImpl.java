package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.dto.MessageCreateDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final MessageService messageService;

    private final ChatRoomMapper chatRoomMapper;

    @Override
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
    public boolean deleteChatRoom(String chatId, String userId) {
        log.info("Deleted chat room with chat id '{}' by user '{}'", chatId, userId);

        checkIfChatExists(chatId);
        checkIfUserMemberOfChat(chatId, userId);

        chatRoomRepository.deleteById(chatId);

        return true;
    }

    @Override
    public Optional<ChatRoom> findChatRoomById(String id) {
        log.info("Find chat room with id '{}'", id);

        return chatRoomRepository.findById(id);
    }

    @Override
    public Page<Message> findMessagesByChatId(String chatId, String userId, Pageable pageable) {
        log.info("Find chat room with chatId '{}' and userId '{}'", chatId, userId);
        checkIfChatExists(chatId);
        checkIfUserMemberOfChat(chatId, userId);

        return messageService.findMessagesByChatId(chatId, pageable);
    }

    @Override
    public Message sendMessage(MessageCreateDto dto) {
        log.info("Send message");

        checkIfChatExists(dto.getChatRoomId());
        checkIfUserMemberOfChat(dto.getChatRoomId(), dto.getUserId());

        return messageService.sendMessage(dto);
    }

    public Message toggleLike(String userId, String messageId, boolean isLike) {
        ChatRoom chatRoomOfMessage = chatRoomRepository.getChatRoomByMessageId(messageId);
        if(chatRoomOfMessage == null) {
            throw new ChatNotFoundException();
        }

        checkIfUserMemberOfChat(userId, chatRoomOfMessage.getId());

        return messageService.toggleLike(userId, messageId, isLike);
    }

    private void checkIfUserMemberOfChat(String chatId, String userId) throws DeniedAccessNotMemberOfChatException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
            .orElseThrow(ChatNotFoundException::new);

        boolean isMemberOfChat = chatRoom.getUsers()
            .stream()
            .anyMatch(u -> u.equals(userId));
        if(!isMemberOfChat) {
            throw new DeniedAccessNotMemberOfChatException(userId);
        }
    }

    private void checkIfChatExists(String chatId) throws ChatNotFoundException {
        if(!chatRoomRepository.existsById(chatId)) {
            throw new ChatNotFoundException();
        }
    }
}
