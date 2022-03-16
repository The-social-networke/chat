package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.mapper.MessageMapper;
import com.socialnetwork.chat.mapper.MessageMapperImpl;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.impl.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class MessageServiceTest {

    @Mock
    private MessageRepository repository;

    @Spy
    private MessageMapper mapper = new MessageMapperImpl();

    @InjectMocks
    private MessageService service;

    private static String chatId;

    private static List<Message> messages;

    @BeforeAll
    static void setUp() {
        chatId = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        messages = List.of(
            new Message()
                .toBuilder()
                .id("e601da78-033e-4d82-8cab-fcd84e2ebeba")
                .userId("1")
                .chatRoom(
                    new ChatRoom()
                        .toBuilder()
                        .id(chatId)
                        .build()
                )
                .text("Hello")
                .sentAt(LocalDateTime.now())
                .isUpdated(false)
                .build(),
            new Message()
                .toBuilder()
                .id("3a47cd5d-0cd1-4690-bf16-32a22b6c9651")
                .userId("2")
                .chatRoom(
                    new ChatRoom()
                        .toBuilder()
                        .id(chatId)
                        .build()
                )
                .text("hi")
                .sentAt(LocalDateTime.now())
                .isUpdated(false)
                .build()
        );
    }

    @Test
    void testSentMessage() {
        Message expectedResult = messages.get(0);
        MessageCreateDto dto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatId)
            .text(expectedResult.getText())
            .build();

        when(repository.save(any(Message.class))).thenReturn(expectedResult);

        Message messageResult = service.sendMessage(dto);

        Assertions.assertEquals(expectedResult, messageResult);
        verify(repository).save(any(Message.class));
    }

    @Test
    void testFindAllByChatId() {
        Page<Message> messagesPage = new PageImpl<>(messages);

        when(repository.findAllByChatRoomId(eq(chatId), any(Pageable.class))).thenReturn(messagesPage);

        Page<Message> messagesResult = service.findMessagesByChatId(chatId, Pageable.ofSize(4));

        Assertions.assertEquals(messagesResult.getContent(), messagesPage.getContent());
        verify(repository).findAllByChatRoomId(eq(chatId), any(Pageable.class));
    }
}
