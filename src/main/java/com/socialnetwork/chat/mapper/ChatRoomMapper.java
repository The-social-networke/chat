package com.socialnetwork.chat.mapper;

import com.socialnetwork.chat.dto.ChatRoomDto;
import com.socialnetwork.chat.entity.ChatRoom;

import java.util.stream.Collectors;

public class ChatRoomMapper {

    public static ChatRoomDto toChatRoomDto(ChatRoom chatRoom) {
        return new ChatRoomDto()
            .toBuilder()
            .id(chatRoom.getId())
            .users(chatRoom.getUsers().stream().map(u -> u.getId().getUserId()).collect(Collectors.toUnmodifiableSet()))
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }

}
