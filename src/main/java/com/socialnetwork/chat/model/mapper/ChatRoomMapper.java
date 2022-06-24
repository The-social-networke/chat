package com.socialnetwork.chat.model.mapper;

import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.ChatRoomUser;
import com.socialnetwork.chat.model.response.ChatRoomInfoRequest;
import com.socialnetwork.chat.model.response.ChatRoomResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomMapper {

    public static ChatRoomResponse toChatRoomDto(ChatRoom chatRoom) {
        return new ChatRoomResponse()
            .toBuilder()
            .id(chatRoom.getId())
            .users(chatRoom.getUsers().stream().map(ChatRoomUser::getUserId).collect(Collectors.toUnmodifiableSet()))
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }

    public static ChatRoomInfoRequest toChatRoomInfoDto(ChatRoom chatRoom, Integer amountOfNotReadMessage) {
        return new ChatRoomInfoRequest()
            .toBuilder()
            .id(chatRoom.getId())
            .users(
                chatRoom.getUsers()
                    .stream()
                    .map(ChatRoomUser::getUserId)
                    .collect(Collectors.toUnmodifiableSet())
            )
            .createdAt(chatRoom.getCreatedAt())
            .amountOfNotReadMessages(amountOfNotReadMessage)
            .build();
    }

}
