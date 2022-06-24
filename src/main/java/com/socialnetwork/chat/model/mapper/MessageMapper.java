package com.socialnetwork.chat.model.mapper;

import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.entity.MessageLike;
import com.socialnetwork.chat.entity.MessageReaders;
import com.socialnetwork.chat.model.request.MessageCreateRequest;
import com.socialnetwork.chat.model.response.MessageRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageMapper {

    public static Message toEntity(MessageCreateRequest dto, String currentUserId) {
        return new Message()
            .toBuilder()
            .text(dto.getText())
            .chatRoom(new ChatRoom().toBuilder().id(dto.getChatRoomId()).build())
            .userId(currentUserId)
            .photo(dto.getPhoto())
            .forwardType(dto.getForwardType())
            .forwardId(dto.getForwardId())
            .build();
    }

    public static MessageRequest toMessageDto(Message message) {
        return new MessageRequest()
            .toBuilder()
            .id(message.getId())
            .text(message.getText())
            .userId(message.getUserId())
            .sentAt(message.getSentAt())
            .isUpdated(message.isUpdated())
            .forwardType(message.getForwardType())
            .forwardId(message.getForwardId())
            .messageLikes(message.getMessageLikes().stream().map(MessageLike::getUserId).collect(Collectors.toSet()))
            .messageReads(message.getMessageReads().stream().map(MessageReaders::getUserId).collect(Collectors.toSet()))
            .messageStatus(message.getMessageStatus())
            .photo(message.getPhoto())
            .build();
    }
}
