package com.socialnetwork.chat.mapper;

import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.dto.MessageDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;

import java.util.stream.Collectors;

public class MessageMapper {

    public static Message toEntity(MessageCreateDto dto) {
        return new Message()
            .toBuilder()
            .text(dto.getText())
            .chatRoom(new ChatRoom().toBuilder().id(dto.getChatRoomId()).build())
            .userId(dto.getCurrentUserId())
            .photo(dto.getPhoto())
            .forwardType(dto.getForwardType())
            .forwardId(dto.getForwardId())
            .build();
    }

    public static MessageDto toMessageDto(Message message) {
        return new MessageDto()
            .toBuilder()
            .id(message.getId())
            .text(message.getText())
            .userId(message.getUserId())
            .sentAt(message.getSentAt())
            .isUpdated(message.isUpdated())
            .forwardType(message.getForwardType())
            .forwardId(message.getForwardId())
            .messageLikes(message.getMessageLikes().stream().map(like -> like.getId().getUserId()).collect(Collectors.toUnmodifiableSet()))
            .messageReads(message.getMessageReads().stream().map(read -> read.getId().getUserId()).collect(Collectors.toUnmodifiableSet()))
            .messageStatus(message.getMessageStatus())
            .photo(message.getPhoto())
            .build();
    }
}
