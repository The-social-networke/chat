package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {


    @Query(value =
        "SELECT EXISTS(" +
            "SELECT *" +
            "   FROM user__chat_room ucr1" +
            "       JOIN user__chat_room ucr2 " +
            "           ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "           AND (ucr1.user_id, ucr2.user_id) = :users" +
            ")",
        nativeQuery = true)
    boolean existsChatRoomByUsers(@Param("users") Set<String> users);

    @Query(value =
        "SELECT cr.* " +
            "FROM user__chat_room ucr1" +
            "   JOIN user__chat_room ucr2" +
            "       ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "       AND (ucr1.user_id, ucr2.user_id) = :users" +
            "   JOIN chat_room cr" +
            "       ON cr.id = ucr1.chat_room_id",
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByUsers(@Param("users") Set<String> users);

    @Query(value =
        "SELECT * FROM chat_room " +
            "WHERE id LIKE " +
            "   (SELECT chat_room_id FROM message" +
            "       WHERE id = :messageId)",
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByMessageId(@Param("messageId") String messageId);
}
