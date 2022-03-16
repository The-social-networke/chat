package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, String> {

    @Query(value =
        "SELECT * FROM message " +
            "WHERE chat_room_id LIKE :chatId " +
            "ORDER BY sent_at",
        nativeQuery = true)
    Page<Message> findAllByChatRoomId(String chatId, Pageable pageable);
}
