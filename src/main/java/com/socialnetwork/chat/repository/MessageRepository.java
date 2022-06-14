package com.socialnetwork.chat.repository;

import com.socialnetwork.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {

    Page<Message> findAllByChatRoomIdOrderBySentAtDesc(String chatId, Pageable pageable);

    Optional<Message> findFirstByChatRoomIdOrderBySentAtDesc(String chatRoomId);
}
