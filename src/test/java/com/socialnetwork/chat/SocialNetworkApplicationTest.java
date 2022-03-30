package com.socialnetwork.chat;


import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
