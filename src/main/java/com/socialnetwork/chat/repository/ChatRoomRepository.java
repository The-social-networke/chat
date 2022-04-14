package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.repository.query.ChatRoomQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query(name = "ChatRoom.findChatRoomsMessage",
        //countName = "ChatRoom.findChatRoomsMessage.count",
        nativeQuery = true)
    Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(@Param("userId") String userId, Pageable pageable);

//    default Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable) {
//        return findChatRoomsMessageByUserId(userId, pageable.getPageSize(), pageable.getPageNumber());
//    }

    @Query(value = ChatRoomQuery.EXISTS_CHAT_ROOM_BY_USERS,
        nativeQuery = true)
    boolean existsChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value = ChatRoomQuery.IS_LAST_MESSAGE_IN_CHAT_ROOM,
        nativeQuery = true)
    boolean isLastMessageInChatRoom(@Param("chatRoomId") String chatRoomId, @Param("messageId") String messageId);

    @Query(value = ChatRoomQuery.FIND_CHAT_ROOM_BY_USERS,
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value = ChatRoomQuery.FIND_CHAT_ROOM_BY_MESSAGE_ID,
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByMessageId(@Param("messageId") String messageId);
}
