package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
import com.socialnetwork.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query(name = "ChatRoomsMessageDtoSql"
        , nativeQuery = true)
    Page<ChatRoomsMessageDto> findChatRoomsMessageByUserId(@Param("userId") String userId, Pageable pageable);

    @Query(value =
        "SELECT EXISTS(" +
            "SELECT *" +
            "   FROM chat.user__chat_room ucr1" +
            "       JOIN chat.user__chat_room ucr2 " +
            "           ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "           AND (ucr1.user_id, ucr2.user_id) = (:userOne, :userTwo)" +
            ")",
        nativeQuery = true)
    boolean existsChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value =
        "SELECT EXISTS(" +
            "    SELECT *" +
            "    FROM (" +
            "        SELECT message.id" +
            "        FROM chat.chat_room" +
            "        JOIN chat.message" +
            "            ON chat.message.chat_room_id = :chatRoomId" +
            "            AND chat_room.id = message.chat_room_id" +
            "        ORDER BY message.sent_at DESC" +
            "        LIMIT 1" +
            "    ) AS last_message" +
            "    WHERE last_message.id = :messageId" +
            ")",
        nativeQuery = true)
    boolean isLastMessageInChatRoom(@Param("chatRoomId") String chatRoomId, @Param("messageId") String messageId);

    @Query(value =
        "SELECT cr.* " +
            "FROM chat.user__chat_room ucr1" +
            "   JOIN chat.user__chat_room ucr2" +
            "       ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "       AND (ucr1.user_id, ucr2.user_id) = (:userOne, :userTwo)" +
            "   JOIN chat.chat_room cr" +
            "       ON cr.id = ucr1.chat_room_id",
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value =
        "SELECT * FROM chat.chat_room " +
            "WHERE id LIKE " +
            "   (SELECT chat_room_id FROM chat.message" +
            "       WHERE id = :messageId)",
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByMessageId(@Param("messageId") String messageId);
}
