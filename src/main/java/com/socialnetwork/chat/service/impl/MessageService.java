package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.dto.MessageLikeDto;
import com.socialnetwork.chat.dto.MessageReadDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatNotFoundException;
import com.socialnetwork.chat.exception.UserLikeHisMessageException;
import com.socialnetwork.chat.exception.UserReadHisMessageException;
import com.socialnetwork.chat.mapper.MessageMapper;
import com.socialnetwork.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Message toggleLikeMessage(MessageLikeDto dto) {
        Message message = messageRepository.findById(dto.getMessageId()).get();

        if(message.getUserId().equals(dto.getUserId())) {
            throw new UserLikeHisMessageException();
        }

        boolean isAlreadyLiked = message.getMessageLikes().contains(dto.getUserId());
        boolean isLikeDto = Boolean.TRUE.equals(dto.getIsLike());
        if(isLikeDto == isAlreadyLiked) {
            return message;
        }
        if(isLikeDto) {
            message.getMessageLikes().add(dto.getUserId());
        }
        else {
            message.getMessageLikes().remove(dto.getUserId());
        }
        return messageRepository.save(message);
    }

    public Message readMessage(MessageReadDto dto) {
        Message message = messageRepository.findById(dto.getMessageId()).get();

        if(message.getUserId().equals(dto.getUserId())) {
            throw new UserReadHisMessageException();
        }

        boolean isAlreadyRead = message.getMessageReads().contains(dto.getUserId());
        if(isAlreadyRead) {
            return message;
        }
        message.getMessageReads().add(dto.getUserId());
        return messageRepository.save(message);
    }
}
