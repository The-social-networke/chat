package com.socialnetwork.chat;


import com.socialnetwork.chat.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
class SocialNetworkApplicationTest {

    @Autowired
    MessageRepository messageRepository;

    @Test
    void contextLoads() {
        messageRepository.getAmountOfMessagesInChatRoomByDate("bbdc2736-8554-45ba-849d-3dee91aba47b", LocalDate.now());
;    }
}
