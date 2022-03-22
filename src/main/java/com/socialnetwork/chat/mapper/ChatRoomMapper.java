package com.socialnetwork.chat.mapper;

import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.entity.ChatRoom;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {

    ChatRoom toEntity(ChatRoomCreateDto dto);
}
