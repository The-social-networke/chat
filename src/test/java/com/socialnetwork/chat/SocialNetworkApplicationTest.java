package com.socialnetwork.chat;


import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
class SocialNetworkApplicationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    void contextLoads() {
        var a = chatRoomRepository.findChatRoomsMessageByUserId("55ab96d7-8a93-4ea3-9d9d-77500018ad4e", PageRequest.of(1, 5));
        System.out.println(a);
        //var b = messageRepository.findAllByChatRoomId("cfdbefcb-012e-4901-97e1-c673335558d7", PageRequest.of(2, 10));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBnbWFpbC5jb20iLCJpYXQiOjE2NDk5MzUzNjUsImV4cCI6MTY0OTkzODk2NX0.NqMyH2aClI4GH1LkYjZuR8tzAujZIl3tGn1RNNjRaGg");
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        restTemplate.exchange("http://198.211.110.141:8002" + "/chat/find-chats", HttpMethod.GET, entity, String.class);
    }
    //SELECT chat.id chatRoomId, message.user_id userId, message.id messageId, message.text as text, message.sent_at sentAt, read_count.amountOfNotReadMessages, another_user.user_id anotherUserId         FROM chat_room chat                 JOIN user__chat_room user_chat                      ON chat.id = user_chat.chat_room_id                          AND user_chat.user_id = ?                 JOIN (                    SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM                    (SELECT * FROM message                    ORDER BY message.chat_room_id, sent_at DESC) ordered_message                 ) AS message                    ON message.chat_room_id = chat.id                JOIN (                    SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages                    FROM message                        JOIN user__chat_room                            ON user__chat_room.user_id = ?                                AND user__chat_room.chat_room_id = message.chat_room_id                        FULL JOIN read_message                            ON read_message.message_id = message.id                                AND read_message.user_id IS NULL                    GROUP BY message.chat_room_id                    ) as read_count                        ON read_count.chat_room_id = message.chat_room_id                 JOIN user__chat_room another_user                      ON user_chat.chat_room_id = another_user.chat_room_id                          AND another_user.user_id != ?         ORDER BY message.sent_at DESC limit ? offset ?
    //SELECT chat.id chatRoomId, message.user_id userId, message.id messageId, message.text as text, message.sent_at sentAt, read_count.amountOfNotReadMessages, another_user.user_id anotherUserId         FROM chat_room chat                 JOIN user__chat_room user_chat                      ON chat.id = user_chat.chat_room_id                          AND user_chat.user_id = ?                 JOIN (                    SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM                    (SELECT * FROM message                    ORDER BY message.chat_room_id, sent_at DESC) ordered_message                 ) AS message                    ON message.chat_room_id = chat.id                JOIN (                    SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages                    FROM message                        JOIN user__chat_room                            ON user__chat_room.user_id = ?                                AND user__chat_room.chat_room_id = message.chat_room_id                        FULL JOIN read_message                            ON read_message.message_id = message.id                                AND read_message.user_id IS NULL                    GROUP BY message.chat_room_id                    ) as read_count                        ON read_count.chat_room_id = message.chat_room_id                 JOIN user__chat_room another_user                      ON user_chat.chat_room_id = another_user.chat_room_id                          AND another_user.user_id != ?         ORDER BY message.sent_at DESC limit ?
}
