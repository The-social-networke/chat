package com.socialnetwork.chat.service.impl;

import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.entity.MessageLike;
import com.socialnetwork.chat.entity.MessageReaders;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.model.enums.ErrorCodeException;
import com.socialnetwork.chat.model.enums.MessageStatus;
import com.socialnetwork.chat.model.mapper.MessageMapper;
import com.socialnetwork.chat.model.request.*;
import com.socialnetwork.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageService {

    private final MessageRepository messageRepository;


    public Page<Message> findMessagesByChatId(String chatId, Pageable pageable) {
        return messageRepository.findAllByChatRoomIdOrderBySentAtDesc(chatId, pageable);
    }

    public Message sendMessage(MessageCreateRequest dto, String currentUserId) {
        var entity = MessageMapper.toEntity(dto, currentUserId)
            .toBuilder()
            .id(UUID.randomUUID().toString())
            .sentAt(LocalDateTime.now())
            .build();

        if (messageRepository.getAmountOfMessagesInChatRoomByDate(dto.getChatRoomId(), LocalDate.now()) == 0) {
            Message systemMessage = new Message()
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .chatRoom(entity.getChatRoom())
                .userId("")
                .isSystem(true)
                .sentAt(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                .build();
            messageRepository.save(systemMessage);
        }

        return messageRepository.save(entity)
            .toBuilder()
            .messageStatus(MessageStatus.SENT)
            .build();
    }

    public Message deleteMessage(MessageDeleteRequest dto, String currentUserId) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if (!message.getUserId().equals(currentUserId)) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_DELETE_NOT_OWN_MESSAGE);
        }

        if (messageRepository.getAmountOfMessagesInChatRoomByDate(message.getChatRoom().getId(), LocalDate.now()) == 1) {
            messageRepository.deleteSystemMessageByDate(message.getChatRoom().getId(), message.getSentAt().toLocalDate());
        }

        messageRepository.delete(message);
        message.setMessageStatus(MessageStatus.DELETED);
        return message;
    }

    public Message readMessage(MessageReadRequest dto, String currentUserId) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(message.getUserId().equals(currentUserId)) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_READ_HIS_MESSAGE);
        }

        boolean isAlreadyRead = message.getMessageReads()
            .stream()
            .anyMatch(obj -> obj.getUserId().equals(currentUserId));
        if(isAlreadyRead) {
            return message;
        }
        message.getMessageReads().add(new MessageReaders(currentUserId, message));
        return messageRepository.save(message)
                .toBuilder()
                .messageStatus(MessageStatus.UPDATED)
                .build();
    }

    public Message updateMessage(MessageUpdateRequest dto, String currentUserId) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(!message.getUserId().equals(currentUserId)) {
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

    public Message toggleLikeMessage(MessageLikeRequest dto, String currentUserId) {
        Message message = messageRepository.findById(dto.getMessageId()).orElseThrow();

        if(message.getUserId().equals(currentUserId)) {
            throw new ChatException(ErrorCodeException.USER_CANNOT_LIKE_HIS_MESSAGE);
        }

        boolean isAlreadyLiked = message.getMessageLikes()
            .stream()
            .anyMatch(obj -> obj.getUserId().equals(currentUserId));
        boolean isLikeDto = Boolean.TRUE.equals(dto.getIsLike());
        if(isLikeDto == isAlreadyLiked) {
            return message;
        }
        if(isLikeDto) {
            message.getMessageLikes().add(
                new MessageLike(currentUserId, message)
            );
        }
        else {
            message.setMessageLikes(
                message.getMessageLikes().stream()
                .filter(m -> !m.getUserId().equals(currentUserId))
                .collect(Collectors.toSet())
            );
        }
        return messageRepository.save(message)
            .toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
    }
}
