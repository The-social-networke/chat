package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.mapper.MessageMapper;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import com.socialnetwork.chat.util.enums.MessageStatus;
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
        Message messageSaved = messageRepository.save(entity);
        messageSaved.setMessageStatus(MessageStatus.SENT);
        return messageSaved;
    }

    public Message deleteMessage(MessageDeleteDto dto) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(!message.getUserId().equals(dto.getCurrentUserId())) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_DELETE_NOT_OWN_MESSAGE);
        }

        messageRepository.delete(message);
        message.setMessageStatus(MessageStatus.DELETED);
        return message;
    }

    public Message readMessage(MessageReadDto dto) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(message.getUserId().equals(dto.getCurrentUserId())) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_READ_HIS_MESSAGE);
        }

        boolean isAlreadyRead = message.getMessageReads().contains(dto.getCurrentUserId());
        if(isAlreadyRead) {
            return message;
        }
        message.getMessageReads().add(dto.getCurrentUserId());
        return messageRepository.save(message)
                .toBuilder()
                .messageStatus(MessageStatus.UPDATED)
                .build();
    }

    public Message updateMessage(MessageUpdateDto dto) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(!message.getUserId().equals(dto.getCurrentUserId())) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_UPDATE_NOT_OWN_MESSAGE);
        }

        message = message.toBuilder()
            .isUpdated(true)
            .text(dto.getText())
            .forwardId(dto.getForwardId())
            .forwardType(dto.getForwardType())
            .build();
        return messageRepository.save(message)
            .toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
    }

    public Message toggleLikeMessage(MessageLikeDto dto) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(message.getUserId().equals(dto.getCurrentUserId())) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_LIKE_HIS_MESSAGE);
        }

        boolean isAlreadyLiked = message.getMessageLikes().contains(dto.getCurrentUserId());
        boolean isLikeDto = Boolean.TRUE.equals(dto.getIsLike());
        if(isLikeDto == isAlreadyLiked) {
            return message;
        }
        if(isLikeDto) {
            message.getMessageLikes().add(dto.getCurrentUserId());
        }
        else {
            message.getMessageLikes().remove(dto.getCurrentUserId());
        }
        return messageRepository.save(message)
            .toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
    }
}
