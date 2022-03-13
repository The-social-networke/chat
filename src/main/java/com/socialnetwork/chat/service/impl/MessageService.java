package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatNotFoundException;
import com.socialnetwork.chat.mapper.MessageMapper;
import com.socialnetwork.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageService {

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    public Page<Message> findMessagesByChatId(String chatId, Pageable pageable) {
        return messageRepository.findAllByChatRoomId(chatId, pageable);
    }

    public Message sendMessage(MessageCreateDto dto) {
        var entity = messageMapper.toEntity(dto)
            .toBuilder()
            .id(UUID.randomUUID().toString())
            .build();
        return messageRepository.save(entity);
    }


    public Message toggleLike(String userId, String messageId, boolean isLike) {
        Message message = messageRepository.getById(messageId);
        boolean isAlreadyLiked = message.getMessageLikes().contains(userId);
        if(isLike == isAlreadyLiked) {
            return message;
        }
        if(isLike) {
            message.getMessageLikes().add(userId);
            return message;
        }
        else {
            message.getMessageLikes().remove(userId);
            return message;
        }
    }
}
