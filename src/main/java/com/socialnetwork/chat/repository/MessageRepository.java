package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.repository.query.MessageQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {

    @Query(value = MessageQuery.EXISTS_MESSAGE_IN_CHAT_ROOM_BY_DATE,
        nativeQuery = true)
    Integer getAmountOfMessagesInChatRoomByDate(@Param("chatRoomId") String chatRoomId, @Param("date") LocalDate localDate);

    Page<Message> findAllByChatRoomIdOrderBySentAtDesc(String chatId, Pageable pageable);

    Optional<Message> findFirstByChatRoomIdOrderBySentAtDesc(String chatRoomId);

    @Query(value = MessageQuery.IS_LAST_MESSAGE_IN_CHAT_ROOM,
        nativeQuery = true)
    boolean isLastMessageInChatRoom(@Param("chatRoomId") String chatRoomId, @Param("messageId") String messageId);

    @Query(value = MessageQuery.IS_LAST_MESSAGE_IN_CHAT_ROOM_BY_DAY,
        nativeQuery = true)
    boolean isLastMessageInChatRoomByDay(@Param("chatRoomId") String chatRoomId, @Param("messageId") String messageId, @Param("date") LocalDate localDate);

    @Modifying
    @Query(value = MessageQuery.DELETE_SYSTEM_MESSAGE_BY_DATE,
        nativeQuery = true)
    void deleteSystemMessageByDate(@Param("chatRoomId") String chatRoomId, @Param("date") LocalDate localDate);
}
