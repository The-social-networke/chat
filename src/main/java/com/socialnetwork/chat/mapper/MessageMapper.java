package com.socialnetwork.chat.mapper;

import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "chatRoomId", target = "chatRoom.id")
    Message toEntity(MessageCreateDto dto);

    MessageCreateDto toDto(Message dto);
}
