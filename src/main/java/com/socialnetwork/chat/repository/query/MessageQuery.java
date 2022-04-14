package com.socialnetwork.chat.repository.query;

public interface MessageQuery {

    String FIND_ALL_BY_CHAT_ROOM_ID =
        "SELECT * FROM message " +
            "WHERE chat_room_id LIKE :chatId " +
            "ORDER BY sent_at";

    String FIND_ALL_BY_CHAT_ROOM_ID_COUNT =
        "SELECT COUNT(*) FROM message " +
            "WHERE chat_room_id LIKE :chatId";


    String FIND_LAST_MESSAGE_IN_CHAT =
            "SELECT message.* " +
                "FROM chat_room" +
                "   JOIN message" +
                "       ON message.chat_room_id = :chatRoomId" +
                "       AND chat_room.id = message.chat_room_id " +
                "ORDER BY message.sent_at DESC " +
                "LIMIT 1";
}
