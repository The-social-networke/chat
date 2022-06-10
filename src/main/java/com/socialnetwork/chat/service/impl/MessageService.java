package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.entity.MessageLikes;
import com.socialnetwork.chat.entity.MessageReaders;
import com.socialnetwork.chat.entity.MessageUserPk;
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

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageService {

    private final MessageRepository messageRepository;


    public Page<Message> findMessagesByChatId(String chatId, Pageable pageable) {
        return messageRepository.findAllByChatRoomId(chatId, pageable);
    }

    public Message sendMessage(MessageCreateDto dto) {
        if (dto.getText().trim().equals("")) {
            throw new ChatException(ErrorCodeException.MESSAGE_CANNOT_BE_EMPTY);
        }

        var entity = MessageMapper.toEntity(dto)
            .toBuilder()
            .id(UUID.randomUUID().toString())
            .sentAt(LocalDateTime.now())
            .build();

        return messageRepository.save(entity)
            .toBuilder()
            .messageStatus(MessageStatus.SENT)
            .build();
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

        boolean isAlreadyRead = message.getMessageReads()
            .stream()
            .anyMatch(obj -> obj.getId().getUserId().equals(dto.getCurrentUserId()));
        if(isAlreadyRead) {
            return message;
        }
        message.getMessageReads().add(new MessageReaders(new MessageUserPk(dto.getCurrentUserId(), message.getId()), message));
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

        if (dto.getText().trim().equals("")) {
            throw new ChatException(ErrorCodeException.MESSAGE_CANNOT_BE_EMPTY);
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

        boolean isAlreadyLiked = message.getMessageLikes()
            .stream()
            .anyMatch(obj -> obj.getId().getUserId().equals(dto.getCurrentUserId()));
        boolean isLikeDto = Boolean.TRUE.equals(dto.getIsLike());
        if(isLikeDto == isAlreadyLiked) {
            return message;
        }
        if(isLikeDto) {
            message.getMessageLikes().add(
                new MessageLikes(new MessageUserPk(dto.getCurrentUserId(), message.getId()), message)
            );
        }
        else {
            message.setMessageLikes(
                message.getMessageLikes().stream()
                .filter(m -> !m.getId().getUserId().equals(dto.getCurrentUserId()))
                .collect(Collectors.toSet())
            );
        }
        return messageRepository.save(message)
            .toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
    }
}
