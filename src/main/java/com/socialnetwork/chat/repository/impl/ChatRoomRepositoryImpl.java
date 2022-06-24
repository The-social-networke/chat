package com.socialnetwork.chat.repository.impl;

import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.model.response.ChatRoomMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ChatRoomRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;


//    public ChatRoomMessageDto findChatRoomMessageByUserIdAndChatIdMap(String userId, String chatId) {
//        String jpqlSql =
//            "SELECT new com.socialnetwork.chat.model.response.ChatRoomMessageDto(" +
//            " chat.id, " + // chatId
//            " chatAnotherUser.chatRoomUserPk.userId, " + // anotherUserId
//            " chatUser.chatRoomUserPk.userId, " + // userId
//            " lastMessage.id, " + // messageId
//            " lastMessage.text, " + // text
//            " lastMessage.sentAt " + // sentat
//            ")" +
//            " FROM ChatRoom chat" +
//            //-- JOIN ALL CHATS FOR USER
//            " JOIN ChatRoomUser chatUser" +
//            "   ON chat.id = chatUser.chatRoomUserPk.chatRoomId" +
//            "   AND chatUser.chatRoomUserPk.userId = :userId" +
//            "   AND chat.id = :chatId" +
//            //-- JOIN ANOTHER USER FOR CHATS
//            " JOIN ChatRoomUser chatAnotherUser" +
//            "   ON chatUser.chatRoomUserPk.chatRoomId = chatAnotherUser.chatRoomUserPk.chatRoomId" +
//            "   AND chatAnotherUser.chatRoomUserPk.userId <> :userId" +
//            //-- JOIN LAST MESSAGE FOR EACH CHAT
//            " JOIN chat.messages lastMessage" +
//            "   ON lastMessage.id = :lastMessageId";
////            //-- JOIN amount of not read message
////            " JOIN chat.messages notReadMessages " +
////            "   ON notReadMessages.chatRoom.id = :chatId" +
////            "   AND notReadMessages.userId <> :userId";
//
//        Query query = entityManager.createQuery(jpqlSql, ChatRoomMessageDto.class);
//        query.setParameter("chatId", "d0d152b8-cffa-4112-b4bd-7e469f5754a4");
//        query.setParameter("userId", "55ab96d7-8a93-4ea3-9d9d-77500018ad4e");
//        query.setParameter("lastMessageId","8010606d-7718-4323-a88e-03bc16827685");
//        return (ChatRoomMessageDto) query.getSingleResult();
//    }

    public boolean existsChatRoomByUsers(String userOne, String userTwo) {
        String jpqlSql =
            " SELECT count(chatUser.chatRoom) " +
            "   FROM ChatRoomUser chatUser" +
            " WHERE chatUser.chatRoomUserPk.userId = :userOne " +
            "   OR chatUser.chatRoomUserPk.userId = :userTwo" +
            " GROUP BY chatUser.chatRoom" +
            " HAVING COUNT(chatUser.chatRoom) = 2";

        TypedQuery<Long> query = entityManager.createQuery(jpqlSql, Long.class);
        query.setParameter("userOne", userOne);
        query.setParameter("userTwo", userTwo);
        query.setMaxResults(1);

        return !query.getResultList().isEmpty();
    }

    public boolean isLastMessageInChatRoom(String chatRoomId, String messageId) {
        String jpqlSql =
            "SELECT messages" +
            "   FROM ChatRoom chat" +
            " JOIN Message messages " +
            "   ON messages.chatRoom.id = chat.id" +
            " WHERE chat.id = :chatRoomId" +
            " GROUP BY messages" +
            " ORDER BY messages.sentAt DESC";

        TypedQuery<Message> query = entityManager.createQuery(jpqlSql, Message.class);
        query.setParameter("chatRoomId", chatRoomId);
        query.setMaxResults(1);

        return query.getResultList()
            .stream()
            .anyMatch(message -> message.getId().equals(messageId));
    }

    public Long getAmountOfAllNotReadMessages(String userId) {
        String jpqlSql =
            "SELECT COUNT(notReadMessages)" +
                " FROM ChatRoom chat" +
                " JOIN chat.messages notReadMessages " +
                "   ON SIZE(notReadMessages.messageReads) = 0" +
                "   AND notReadMessages.userId <> :userId" +
                " JOIN ChatRoomUser chatUser" +
                "   ON chatUser.chatRoomUserPk.userId = : userId" +
                "   AND chat.id = chatUser.chatRoomUserPk.chatRoomId";

        TypedQuery<Long> query = entityManager.createQuery(jpqlSql, Long.class);
        query.setParameter("userId", userId);

        return query.getSingleResult();
    }

    public Long getChatRoomsCount(String userId) {
        String nativeSql =
            "SELECT COUNT(chatUser) " +
                " FROM ChatRoomUser chatUser" +
                " WHERE chatUser.chatRoomUserPk.userId like :userId ";

        TypedQuery<Long> query = entityManager.createQuery(nativeSql, Long.class);
        query.setParameter("userId", userId);

        return query.getResultList()
            .stream()
            .findFirst()
            .orElseThrow();
    }

    public Optional<ChatRoom> findChatRoomByUsers(String userOne, String userTwo) {
        String jpqlSql =
            "SELECT chat" +
                " FROM ChatRoom chat" +
                " JOIN ChatRoomUser chatUser" +
                "   ON chatUser.chatRoomUserPk.userId = :userOne " +
                "   OR chatUser.chatRoomUserPk.userId = :userTwo" +
                " GROUP BY chatUser.chatRoom, chat" +
                "   HAVING COUNT(chatUser.chatRoom) = 2";

        TypedQuery<ChatRoom> query = entityManager.createQuery(jpqlSql, ChatRoom.class);
        query.setParameter("userOne", userOne);
        query.setParameter("userTwo", userTwo);
        query.setMaxResults(1);

        return query.getResultList()
            .stream()
            .findFirst();
    }

    public Optional<ChatRoom> findChatRoomByMessageId(@Param("messageId") String messageId) {
        String jpqlSql =
            "SELECT chat" +
                " FROM ChatRoom chat" +
                " JOIN Message messages" +
                "   ON messages.chatRoom.id = chat.id" +
                "   AND messages.id = :messageId";

        TypedQuery<ChatRoom> query = entityManager.createQuery(jpqlSql, ChatRoom.class);
        query.setParameter("messageId", messageId);

        return query.getResultList()
            .stream().findFirst();
    }

    public ChatRoomMessageRequest getChatRoomMessageByUserIdAndChatId(String userId, String chatId) {
        String nativeSql =
        "SELECT chat.id chatRoomId, message.user_id userId, another_user.user_id anotherUserId, message.id messageId, message.text as text, message.sent_at sentAt, read_count.amountOfNotReadMessages" +
            " FROM chat_room chat" +
            //" -- JOIN ALL CHATS FOR USER" +
            "   JOIN user__chat_room user_chat" +
            "       ON chat.id = user_chat.chat_room_id" +
            "       AND user_chat.user_id = :userId" +
            "       AND chat.id = :chatId" +
            //"  -- JOIN ANOTHER USER FOR CHATS" +
            "    JOIN user__chat_room another_user" +
            "        ON user_chat.chat_room_id = another_user.chat_room_id" +
            "        AND another_user.user_id != :userId" +
            //"    -- JOIN LAST MESSAGE FOR CHAT" +
            "    LEFT JOIN" +
            "    (" +
            //"        -- SELECT UNIQUE MESSAGE FOR EACH CHAT" +
            "        SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM" +
            "        (" +
            //"            -- SELECT ALL SORTED MESSAGE" +
            "            SELECT * FROM message" +
            "            ORDER BY message.chat_room_id, sent_at DESC" +
            "        ) ordered_message" +
            "    ) AS message" +
            "        ON message.chat_room_id = chat.id" +
            //"    -- JOIN AMOUNT OF NOT READ MESSAGE FOR ALL USER'S CHATS" +
            "    LEFT JOIN (" +
            //"        -- SELECT AMOUNT OF NOT READ MESSAGE FOR ALL USER'S CHATS" +
            "        SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages" +
            "        FROM message" +
            //"            -- JOIN ALL MESSAGE FOR ALL CHATS" +
            "            JOIN user__chat_room" +
            "                ON user__chat_room.user_id = :userId" +
            "                AND user__chat_room.chat_room_id = message.chat_room_id" +
            "                AND message.user_id != :userId" +
            //"            -- JOIN ALL READ MESSAGE" +
            "            LEFT JOIN read_message" +
            "                ON read_message.message_id = message.id" +
            "            WHERE read_message.user_id IS NULL" +
            "        GROUP BY message.chat_room_id" +
            "    ) as read_count" +
            "        ON read_count.chat_room_id = message.chat_room_id ";
        Query query = entityManager.createNativeQuery(nativeSql);
        query.setParameter("userId", userId);
        query.setParameter("chatId", chatId);

        List<Object[]> result = query.getResultList();

        return result.stream()
            .map(this::getChatRoomMessageFromMap)
            .findFirst()
            .orElseThrow();
    }

    public Page<ChatRoomMessageRequest> findChatRoomsMessageByUserId(String userId, Pageable pageable) {
        String nativeSql =
        "SELECT chat.id chatRoomId, message.user_id userId, message.id messageId, message.text as text, message.sent_at sentAt, read_count.amountOfNotReadMessages, another_user.user_id anotherUserId " +
            //"-- SELECT ALL CHATS " +
            "FROM chat_room chat " +
            //"    -- JOIN ALL CHATS FOR USER" +
            "    JOIN user__chat_room user_chat" +
            "        ON chat.id = user_chat.chat_room_id" +
            "        AND user_chat.user_id = :userId" +
            //"    -- JOIN ANOTHER USER FOR CHATS" +
            "    JOIN user__chat_room another_user" +
            "        ON user_chat.chat_room_id = another_user.chat_room_id" +
            "        AND another_user.user_id != :userId" +
            //"    -- JOIN LAST MESSAGE FOR CHAT" +
            "    LEFT JOIN" +
            "    (" +
            //"        -- SELECT UNIQUE MESSAGE FOR EACH CHAT" +
            "        SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM" +
            "        (" +
            //"            -- SELECT ALL SORTED MESSAGE" +
            "            SELECT * FROM message" +
            "            ORDER BY message.chat_room_id, sent_at DESC" +
            "        ) ordered_message" +
            "    ) AS message" +
            "        ON message.chat_room_id = chat.id" +
            //"    -- JOIN AMOUNT OF NOT READ MESSAGE FOR ALL USER'S CHATS" +
            "    LEFT JOIN (" +
            //"        -- SELECT AMOUNT OF NOT READ MESSAGE FOR ALL USER'S CHATS" +
            "        SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages" +
            "        FROM message" +
            //"            -- JOIN ALL MESSAGE FOR ALL CHATS" +
            "            JOIN user__chat_room" +
            "                ON user__chat_room.user_id = :userId" +
            "                AND user__chat_room.chat_room_id = message.chat_room_id" +
            "                AND message.user_id != :userId" +
            //"            -- JOIN ALL READ MESSAGE" +
            "            LEFT JOIN read_message" +
            "                ON read_message.message_id = message.id" +
            "            WHERE read_message.user_id IS NULL" +
            "        GROUP BY message.chat_room_id" +
            "    ) as read_count" +
            "        ON read_count.chat_room_id = message.chat_room_id " +
            "ORDER BY message.sent_at DESC NULLS LAST " +
            "LIMIT :limit OFFSET :offset";

        Query query = entityManager.createNativeQuery(nativeSql);
        query.setParameter("userId", userId);
        query.setParameter("limit", pageable.getPageSize());
        query.setParameter("offset", pageable.getOffset());

        List<Object[]> result = query.getResultList();

        var listResult = result.stream()
            .map(this::getChatRoomMessageFromMap)
            .collect(Collectors.toList());
        return new PageImpl<>(listResult, pageable, getChatRoomsCount(userId));
    }

    private ChatRoomMessageRequest getChatRoomMessageFromMap(Object[] objects) {
        return new ChatRoomMessageRequest()
            .toBuilder()
            .chatRoomId(objects[0] == null ? null : (String) objects[0])
            .userId(objects[1] == null ? null : (String) objects[1])
            .anotherUserId(objects[2] == null ? null : (String) objects[2])
            .messageId(objects[3] == null ? null : (String) objects[3])
            .text(objects[4] == null ? null : (String) objects[4])
            .sentAt(objects[5] == null ? null : ((Timestamp) objects[5]).toLocalDateTime())
            .amountOfNotReadMessages(objects[6] == null ? 0 : ((BigInteger) objects[6]).longValue())
            .build();
    }
}
