package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.repository.query.MessageQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {

    @Query(value = MessageQuery.FIND_ALL_BY_CHAT_ROOM_ID,
        countQuery = MessageQuery.FIND_ALL_BY_CHAT_ROOM_ID_COUNT,
        nativeQuery = true)
    Page<Message> findAllByChatRoomId(String chatId, Pageable pageable);

    @Query(value = MessageQuery.FIND_LAST_MESSAGE_IN_CHAT,
        nativeQuery = true)
    Optional<Message> findLastMessageInChat(@Param("chatRoomId") String chatRoomId);
}
