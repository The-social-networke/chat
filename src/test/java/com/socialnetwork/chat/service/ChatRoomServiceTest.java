package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.mapper.ChatRoomMapperImpl;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import com.socialnetwork.chat.service.impl.MessageService;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(MockitoJUnitRunner.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository repository;

    @Mock
    private MessageService messageService;

    @Spy
    private ChatRoomMapperImpl mapper;

    @InjectMocks
    private ChatRoomServiceImpl service;

    private static List<String> users;

    private static List<ChatRoom> chatRooms;

    @BeforeAll
    static void setUp() {
        users = List.of(
            "ff2ce835-e775-40d2-a1b6-4e382b8e465c",
            "d3c5c1a2-f69b-49f6-93ea-68e29adce0d1",
            "027f8c23-8922-457d-b29b-6cb33b59684c"
        );
        chatRooms = List.of(
            new ChatRoom()
                .toBuilder()
                .id("288f7ad6-7d88-4a1e-8c5c-ebfcdcc58847")
                .createdAt(LocalDateTime.now())
                .users(Set.of(users.get(0), users.get(1)))
                .messages(Set.of(
                    new Message()
                        .toBuilder()
                        .text("message 1 from user 1")
                        .userId(users.get(0))
                        .sentAt(LocalDateTime.now())
                        .chatRoom(
                            new ChatRoom()
                                .toBuilder()
                                .id("288f7ad6-7d88-4a1e-8c5c-ebfcdcc58847")
                                .build()
                        )
                        .messageLikes(Set.of(users.get(1)))
                        .build(),
                    new Message()
                        .toBuilder()
                        .text("message 2 from user 2")
                        .userId(users.get(1))
                        .sentAt(LocalDateTime.now())
                        .chatRoom(
                            new ChatRoom()
                                .toBuilder()
                                .id("288f7ad6-7d88-4a1e-8c5c-ebfcdcc58847")
                                .build()
                        )
                        .messageLikes(Set.of(users.get(0)))
                        .build()
                ))
                .build(),
            new ChatRoom()
                .toBuilder()
                .id("09706be4-8462-4ddc-be31-5a8321fdc485")
                .createdAt(LocalDateTime.now())
                .users(Set.of(users.get(1), users.get(2)))
                .messages(Set.of(
                    new Message()
                        .toBuilder()
                        .text("message 1 from user 2")
                        .userId(users.get(1))
                        .sentAt(LocalDateTime.now())
                        .chatRoom(
                            new ChatRoom()
                                .toBuilder()
                                .id("09706be4-8462-4ddc-be31-5a8321fdc485")
                                .build()
                        )
                        .messageLikes(Set.of(users.get(2)))
                        .build(),
                    new Message()
                        .toBuilder()
                        .text("message 2 from user 3")
                        .userId(users.get(2))
                        .sentAt(LocalDateTime.now())
                        .chatRoom(
                            new ChatRoom()
                                .toBuilder()
                                .id("09706be4-8462-4ddc-be31-5a8321fdc485")
                                .build()
                        )
                        .messageLikes(Set.of(users.get(1)))
                        .build()
                ))
                .build()
        );
    }

    @Test
    void testCreateChatRoomIfNotExists() {
        ChatRoom expectChatRoom = chatRooms.get(0);

        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(false);
        when(repository.save(any(ChatRoom.class))).thenReturn(expectChatRoom);

        ChatRoom chatRoomResult = service.createChatRoom(users.get(0), users.get(1));

        Assertions.assertEquals(chatRoomResult, expectChatRoom);
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
        verify(repository).save(any(ChatRoom.class));
    }

    @Test
    void testCreateChatRoomIfExists() {
        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(true);

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.createChatRoom(users.get(0), users.get(1))
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_WITH_THESE_USERS_ALREADY_EXISTS, thrown.getErrorCodeException());
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
    }

    @Test
    void testDeleteChatRoomIfChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);

        when(repository.existsById(chatId)).thenReturn(true);
        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        boolean result = service.deleteChatRoom(chatId, userId);

        Assertions.assertTrue(result);
        verify(repository).existsById(chatId);
        verify(repository).findById(chatId);
    }

    @Test
    void testDeleteChatRoomIfChatExistsAndUserNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);

        when(repository.existsById(chatId)).thenReturn(true);
        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteChatRoom(chatId, userId)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).existsById(chatId);
        verify(repository).findById(chatId);
    }

    @Test
    void testDeleteChatRoomIfChatNotExistsAndUserNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();

        when(repository.existsById(chatId)).thenReturn(false);

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteChatRoom(chatId, userId)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).existsById(chatId);
    }

    @Test
    void testFindChatRoomById() {
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        Optional<ChatRoom> chatRoomResult = service.findChatRoomById(chatId);

        Assertions.assertEquals(chatRoomResult.get(), chatRoomExpect);
        verify(repository).findById(chatId);
    }

    @Test
    void testFindMessagesByChatIdIfChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);
        Page<Message> messagesPage = new PageImpl<>(new ArrayList<>(chatRooms.get(0).getMessages()));

        when(repository.existsById(chatId)).thenReturn(true);
        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));
        when(messageService.findMessagesByChatId(chatId, Pageable.ofSize(4))).thenReturn(messagesPage);

        Page<Message>  messagesResult = service.findMessagesByChatId(chatId, userId, Pageable.ofSize(4));

        Assertions.assertEquals(messagesPage.getContent(), messagesResult.getContent());
        verify(repository).existsById(chatId);
        verify(repository).findById(chatId);
        verify(messageService).findMessagesByChatId(chatId, Pageable.ofSize(4));
    }

    @Test
    void testFindMessagesByChatIdIfChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(1).getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);

        when(repository.existsById(chatId)).thenReturn(true);
        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        var pageable = Pageable.ofSize(4);
        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.findMessagesByChatId(chatId, userId, pageable)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).existsById(chatId);
        verify(repository).findById(chatId);
    }

    @Test
    void testFindMessagesByChatIdIfChatNotExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();

        when(repository.existsById(chatId)).thenReturn(false);

        var pageable = Pageable.ofSize(4);
        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.findMessagesByChatId(chatId, userId, pageable)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).existsById(chatId);
    }

    @Test
    void testSendMessageIfChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRoomExpect.getId())
            .userId(userId)
            .text("some text")
            .build();
        Message messageExpect = new Message()
            .toBuilder()
            .id("3bb05f77-2b8f-4bf6-969d-8179cb298e69")
            .sentAt(LocalDateTime.now())
            .isUpdated(false)
            .chatRoom(chatRoomExpect)
            .userId(userId)
            .text("some text")
            .build();

        when(repository.existsById(chatId)).thenReturn(true);
        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));
        when(messageService.sendMessage(messageCreateDto)).thenReturn(messageExpect);

        Message messageResult = service.sendMessage(messageCreateDto);

        Assertions.assertEquals(messageResult, messageExpect);
        verify(repository).existsById(chatId);
        verify(repository).findById(chatId);
        verify(messageService).sendMessage(messageCreateDto);
    }

    @Test
    void testSendMessageIfChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(1).getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRoomExpect.getId())
            .userId(userId)
            .text("some text")
            .build();

        when(repository.existsById(chatId)).thenReturn(true);
        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.sendMessage(messageCreateDto)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).existsById(chatId);
        verify(repository).findById(chatId);
    }

    @Test
    void testSendMessageIfChatNotExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(1).getId();
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRooms.get(1).getId())
            .userId(userId)
            .text("some text")
            .build();

        when(repository.existsById(chatId)).thenReturn(false);

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.sendMessage(messageCreateDto)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).existsById(chatId);
    }
}
