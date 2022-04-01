package com.socialnetwork.chat;


import com.socialnetwork.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
class SocialNetworkApplicationTest {

    @Autowired
    private ChatRoomRepository repository;

    @Test
    void contextLoads() {
    }
}
