package com.socialnetwork.chat.repository.query;

public interface MessageQuery {

    String EXISTS_MESSAGE_IN_CHAT_ROOM_BY_DATE =
        "SELECT COUNT(*)" +
            " FROM message" +
            " WHERE message.chat_room_id = :chatRoomId" +
            "    AND cast(sent_at as date) = TO_DATE(:date, 'YYYY-MM-DD')" +
            "    AND message.is_system = false";

    String IS_LAST_MESSAGE_IN_CHAT_ROOM =
        "SELECT EXISTS(" +
            "    SELECT *" +
            "    FROM (" +
            "        SELECT message.id" +
            "        FROM chat_room" +
            "        JOIN message" +
            "            ON message.chat_room_id = :chatRoomId" +
            "            AND chat_room.id = message.chat_room_id" +
            "            AND message.is_system = false" +
            "        ORDER BY message.sent_at DESC" +
            "        LIMIT 1" +
            "    ) AS last_message" +
            "    WHERE last_message.id = :messageId" +
            ")";

    String IS_LAST_MESSAGE_IN_CHAT_ROOM_BY_DAY =
        "SELECT EXISTS(" +
            "    SELECT *" +
            "    FROM (" +
            "        SELECT message.id" +
            "        FROM chat_room" +
            "        JOIN message" +
            "            ON message.chat_room_id = :chatRoomId" +
            "            AND chat_room.id = message.chat_room_id" +
            "            AND message.is_system = false" +
            "            AND cast(sent_at as date) = TO_DATE(:date, 'YYYY-MM-DD')" +
            "        ORDER BY message.sent_at DESC" +
            "        LIMIT 1" +
            "    ) AS last_message" +
            "    WHERE last_message.id = :messageId" +
            ")";

    String DELETE_SYSTEM_MESSAGE_BY_DATE =
        "DELETE FROM message" +
            " WHERE is_system = true" +
            "   AND chat_room_id = :chatRoomId" +
            "   AND cast(sent_at as date) = TO_DATE(:date, 'YYYY-MM-DD')";
}
