package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {

    @Query(value =
        "SELECT * FROM message " +
            "WHERE chat_room_id LIKE :chatId " +
            "ORDER BY sent_at",
        nativeQuery = true)
    Page<Message> findAllByChatRoomId(String chatId, Pageable pageable);

    @Query(value =
        "SELECT message.* " +
            "FROM chat_room" +
            "   JOIN message" +
            "       ON message.chat_room_id = :chatRoomId" +
            "       AND chat_room.id = message.chat_room_id " +
            "ORDER BY message.sent_at DESC " +
            "LIMIT 1",
        nativeQuery = true)
    Optional<Message> findLastMessageInChat(@Param("chatRoomId") String chatRoomId);
}
