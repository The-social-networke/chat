package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {


    @Query(value =
        "SELECT EXISTS(" +
            "SELECT *" +
            "   FROM user__chat_room ucr1" +
            "       JOIN user__chat_room ucr2 " +
            "           ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "           AND (ucr1.user_id, ucr2.user_id) = (:userOne, :userTwo)" +
            ")",
        nativeQuery = true)
    boolean existsChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value =
        "SELECT cr.* " +
            "FROM user__chat_room ucr1" +
            "   JOIN user__chat_room ucr2" +
            "       ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "       AND (ucr1.user_id, ucr2.user_id) = (:userOne, :userTwo)" +
            "   JOIN chat_room cr" +
            "       ON cr.id = ucr1.chat_room_id",
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByUsers(@Param("userOne") String userOne, @Param("userTwo") String userTwo);

    @Query(value =
        "SELECT * FROM chat_room " +
            "WHERE id LIKE " +
            "   (SELECT chat_room_id FROM message" +
            "       WHERE id = :messageId)",
        nativeQuery = true)
    Optional<ChatRoom> findChatRoomByMessageId(@Param("messageId") String messageId);
}
