package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.repository.query.ChatRoomQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query(value = ChatRoomQuery.FIND_CHAT_ROOMS_MESSAGE,
        nativeQuery = true)
    List<Map<String, Object>> findChatRoomsMessageByUserId(@Param("userId") String userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = ChatRoomQuery.FIND_CHAT_ROOMS_COUNT_FOR_USER,
        nativeQuery = true)
    int findChatRoomsCount(@Param("userId") String userId);

    default Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(String userId, Pageable pageable) {
        var resultObjects = findChatRoomsMessageByUserId(userId, pageable.getPageSize(), (int) pageable.getOffset());
        var content = resultObjects.stream()
            .map(map -> new ChatRoomsMessageDto()
                        .toBuilder()
                        .chatRoomId(map.get("chatRoomId") == null ? null : (String) map.get("chatRoomId"))
                        .anotherUserId(map.get("anotherUserId") == null ? null : (String) map.get("anotherUserId"))
                        .userId(map.get("userId") == null ? null : (String) map.get("userId"))
                        .messageId(map.get("messageId") == null ? null : (String) map.get("messageId"))
                        .text(map.get("text") == null ? null : (String) map.get("text"))
                        .sentAt(map.get("sentAt") == null ? null : ((Timestamp) map.get("sentAt")).toLocalDateTime())
                        .amountOfNotReadMessages(map.get("amountOfNotReadMessages") == null ? 0 : ((BigInteger) map.get("amountOfNotReadMessages")).intValue())
                        .build()
            )
            .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, findChatRoomsCount(userId));
    }

    @Query(value = ChatRoomQuery.EXISTS_CHAT_ROOM_BY_USERS,
        nativeQuery = true)
    boolean existsChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value = ChatRoomQuery.IS_LAST_MESSAGE_IN_CHAT_ROOM,
        nativeQuery = true)
    boolean isLastMessageInChatRoom(@Param("chatRoomId") String chatRoomId, @Param("messageId") String messageId);

    @Query(value = ChatRoomQuery.GET_AMOUNT_OF_NOT_READ_MESSAGE,
        nativeQuery = true)
    Integer getAmountOfNotReadMessages(@Param("chatRoomId") String chatRoomId);

    @Query(value = ChatRoomQuery.FIND_CHAT_ROOM_BY_USERS,
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value = ChatRoomQuery.FIND_CHAT_ROOM_BY_MESSAGE_ID,
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByMessageId(@Param("messageId") String messageId);
}
