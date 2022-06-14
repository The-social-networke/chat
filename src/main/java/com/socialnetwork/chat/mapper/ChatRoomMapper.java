package com.socialnetwork.chat.mapper;

import com.socialnetwork.chat.dto.ChatRoomDto;
import com.socialnetwork.chat.dto.ChatRoomInfoDto;
import com.socialnetwork.chat.entity.ChatRoom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomMapper {

    public static ChatRoomDto toChatRoomDto(ChatRoom chatRoom) {
        return new ChatRoomDto()
            .toBuilder()
            .id(chatRoom.getId())
            .users(chatRoom.getUsers().stream().map(u -> u.getChatRoomUserPk().getUserId()).collect(Collectors.toUnmodifiableSet()))
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }

    public static ChatRoomInfoDto toChatRoomInfoDto(ChatRoom chatRoom, Integer amountOfNotReadMessage) {
        return new ChatRoomInfoDto()
            .toBuilder()
            .id(chatRoom.getId())
            .users(
                chatRoom.getUsers()
                    .stream()
                    .map(obj -> obj.getChatRoomUserPk().getUserId())
                    .collect(Collectors.toUnmodifiableSet())
            )
            .createdAt(chatRoom.getCreatedAt())
            .amountOfNotReadMessages(amountOfNotReadMessage)
            .build();
    }

}
