package com.socialnetwork.chat.repository.query;

public interface ChatRoomQuery {

    String FIND_CHAT_ROOMS_MESSAGE =
        "SELECT chat.id chatRoomId, message.user_id userId, message.id messageId, message.text as text, message.sent_at sentAt, read_count.amountOfNotReadMessages, another_user.user_id anotherUserId" +
            "         FROM chat_room chat" +
            "                 JOIN user__chat_room user_chat" +
            "                      ON chat.id = user_chat.chat_room_id" +
            "                          AND user_chat.user_id = :userId" +
            "                 JOIN (" +
            "                    SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM" +
            "                    (SELECT * FROM message" +
            "                    ORDER BY message.chat_room_id, sent_at DESC) ordered_message" +
            "                 ) AS message" +
            "                    ON message.chat_room_id = chat.id" +
            "                JOIN (" +
            "                    SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages" +
            "                    FROM message" +
            "                        JOIN user__chat_room" +
            "                            ON user__chat_room.user_id = :userId" +
            "                                AND user__chat_room.chat_room_id = message.chat_room_id" +
            "                        FULL JOIN read_message" +
            "                            ON read_message.message_id = message.id" +
            "                                AND read_message.user_id IS NULL" +
            "                    GROUP BY message.chat_room_id" +
            "                    ) as read_count" +
            "                        ON read_count.chat_room_id = message.chat_room_id" +
            "                 JOIN user__chat_room another_user" +
            "                      ON user_chat.chat_room_id = another_user.chat_room_id" +
            "                          AND another_user.user_id != :userId" +
            "         ORDER BY message.sent_at DESC ";

    String FIND_CHAT_ROOMS_MESSAGE_COUNT =
        "SELECT COUNT(chat.id) " +
            "         FROM chat_room chat" +
            "                 JOIN user__chat_room user_chat" +
            "                      ON chat.id = user_chat.chat_room_id" +
            "                          AND user_chat.user_id = :userId" +
            "                 JOIN (" +
            "                    SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM" +
            "                    (SELECT * FROM message" +
            "                    ORDER BY message.chat_room_id, sent_at DESC) ordered_message" +
            "                 ) AS message" +
            "                    ON message.chat_room_id = chat.id" +
            "                JOIN (" +
            "                    SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages" +
            "                    FROM message" +
            "                        JOIN user__chat_room" +
            "                            ON user__chat_room.user_id = :userId" +
            "                                AND user__chat_room.chat_room_id = message.chat_room_id" +
            "                        FULL JOIN read_message" +
            "                            ON read_message.message_id = message.id" +
            "                                AND read_message.user_id IS NULL" +
            "                    GROUP BY message.chat_room_id" +
            "                    ) as read_count" +
            "                        ON read_count.chat_room_id = message.chat_room_id" +
            "                 JOIN user__chat_room another_user" +
            "                      ON user_chat.chat_room_id = another_user.chat_room_id" +
            "                          AND another_user.user_id != :userId";


    String EXISTS_CHAT_ROOM_BY_USERS =
        "SELECT EXISTS(" +
            "SELECT *" +
            "   FROM user__chat_room ucr1" +
            "       JOIN user__chat_room ucr2 " +
            "           ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "           AND (ucr1.user_id, ucr2.user_id) = (:userOne, :userTwo)" +
            ")";


    String IS_LAST_MESSAGE_IN_CHAT_ROOM =
    "SELECT EXISTS(" +
        "    SELECT *" +
        "    FROM (" +
        "        SELECT message.id" +
        "        FROM chat_room" +
        "        JOIN message" +
        "            ON message.chat_room_id = :chatRoomId" +
        "            AND chat_room.id = message.chat_room_id" +
        "        ORDER BY message.sent_at DESC" +
        "        LIMIT 1" +
        "    ) AS last_message" +
        "    WHERE last_message.id = :messageId" +
        ")";


    String FIND_CHAT_ROOM_BY_USERS =
        "SELECT cr.* " +
            "FROM user__chat_room ucr1" +
            "   JOIN user__chat_room ucr2" +
            "       ON ucr1.chat_room_id = ucr2.chat_room_id" +
            "       AND (ucr1.user_id, ucr2.user_id) = (:userOne, :userTwo)" +
            "   JOIN chat_room cr" +
            "       ON cr.id = ucr1.chat_room_id";


    String FIND_CHAT_ROOM_BY_MESSAGE_ID =
        "SELECT * FROM chat_room " +
            "WHERE id LIKE " +
            "   (SELECT chat_room_id FROM message" +
            "       WHERE id = :messageId)";
}
