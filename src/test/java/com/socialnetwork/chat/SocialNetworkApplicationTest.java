package com.socialnetwork.chat;


import com.socialnetwork.chat.repository.impl.ChatRoomRepositoryImpl;
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
    ChatRoomRepositoryImpl chatRoomRepository;

    @Test
    void contextLoads() {
        var result = chatRoomRepository.findChatRoomByUsers("d0d152b8-cffa-4112-b4bd-7e469f5754a4", "2c055e8d-8bee-45cb-ade5-a9f721166904");
        var result2 = chatRoomRepository.findChatRoomByUsers("52d9f27d-32f7-4312-8a35-4c8d2e0cb49a", "55ab96d7-8a93-4ea3-9d9d-77500018ad4e");
        var result3 = chatRoomRepository.getChatRoomMessageByUserIdAndChatId("55ab96d7-8a93-4ea3-9d9d-77500018ad4e", "d0d152b8-cffa-4112-b4bd-7e469f5754a4");
        System.out.println(result);
    }
}
