package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import com.socialnetwork.chat.service.impl.MessageService;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(MockitoJUnitRunner.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository repository;

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ChatRoomServiceImpl service;

    private List<String> users;

    private List<ChatRoom> chatRooms;

    private String urlAuth;

    private String systemUserId;

    @BeforeEach
    void setUp() {
        urlAuth = "http://198.211.110.141:3000/user/exists_by_id?userId=";
        ReflectionTestUtils.setField(service, "url", "http://198.211.110.141:3000");

        systemUserId = "8a744b81-38fd-4fe1-a032-33836e7a0221";
        ReflectionTestUtils.setField(service, "systemUserId", systemUserId);

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
                .build(),
            new ChatRoom()
                .toBuilder()
                .id("19706be4-8499-4ddc-ab77-3w8321djc005")
                .createdAt(LocalDateTime.now())
                .users(Set.of(users.get(0), systemUserId))
                .messages(Set.of(
                    new Message()
                        .toBuilder()
                        .text("message 1 from user 1")
                        .userId(users.get(0))
                        .sentAt(LocalDateTime.now())
                        .chatRoom(
                            new ChatRoom()
                                .toBuilder()
                                .id("19706be4-8499-4ddc-ab77-3w8321djc005")
                                .build()
                        )
                        .messageLikes(Set.of(systemUserId))
                        .build(),
                    new Message()
                        .toBuilder()
                        .text("message 2 from system user")
                        .userId(systemUserId)
                        .sentAt(LocalDateTime.now())
                        .chatRoom(
                            new ChatRoom()
                                .toBuilder()
                                .id("19706be4-8499-4ddc-ab77-3w8321djc005")
                                .build()
                        )
                        .messageLikes(Set.of(users.get(0)))
                        .build()
                ))
                .build()
        );
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
    void testGetChatRoomByUsersOrElseCreate_ifChatRoomIsExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        ChatRoom expectChatRoom = chatRooms.get(0);

        when(repository.findChatRoomByUsers(users.get(0), users.get(1))).thenReturn(Optional.of(expectChatRoom));

        ChatRoom chatRoomResult = service.getChatRoomByUsersOrElseCreate(dto);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).findChatRoomByUsers(users.get(0), users.get(1));
    }

    @Test
    void testGetChatRoomByUsersOrElseCreate_ifChatRoomIsNotExistAndUserExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        ChatRoom expectChatRoom = chatRooms.get(0);

        when(repository.findChatRoomByUsers(users.get(0), users.get(1))).thenReturn(Optional.empty());
        when(restTemplate.exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class)).thenReturn(getSuccessResponse(true));
        when(repository.save(any(ChatRoom.class))).thenReturn(expectChatRoom);

        ChatRoom chatRoomResult = service.getChatRoomByUsersOrElseCreate(dto);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).findChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class);
        verify(repository).save(any(ChatRoom.class));
    }

    @Test
    void testGetChatRoomByUsersOrElseCreate_ifChatRoomIsNotExistAndUserNotExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();

        when(repository.findChatRoomByUsers(users.get(0), users.get(1))).thenReturn(Optional.empty());
        when(restTemplate.exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class)).thenReturn(getSuccessResponse(false));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.getChatRoomByUsersOrElseCreate(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class);
    }


    @Test
    void testGetSystemChatRoomByUsersOrElseCreate_ifChatRoomIsExists() {
        String userId = users.get(0);
        ChatRoom expectChatRoom = chatRooms.get(2);

        when(repository.findChatRoomByUsers(userId, systemUserId)).thenReturn(Optional.of(expectChatRoom));

        ChatRoom chatRoomResult = service.getSystemChatRoomByUserOrElseCreate(userId);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).findChatRoomByUsers(userId, systemUserId);
    }

    @Test
    void testGetSystemChatRoomByUsersOrElseCreate_ifChatRoomIsNotExistAndUserExists() {
        String userId = users.get(0);
        ChatRoom expectChatRoom = chatRooms.get(2);

        when(repository.findChatRoomByUsers(userId, systemUserId)).thenReturn(Optional.empty());
        when(repository.save(any(ChatRoom.class))).thenReturn(expectChatRoom);

        ChatRoom chatRoomResult = service.getSystemChatRoomByUserOrElseCreate(userId);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).findChatRoomByUsers(userId, systemUserId);
        verify(repository).save(any(ChatRoom.class));
    }


    @Test
    void testFindMessagesByChatId_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);
        Page<Message> messagesPage = new PageImpl<>(new ArrayList<>(chatRooms.get(0).getMessages()));

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));
        when(messageService.findMessagesByChatId(chatId, Pageable.ofSize(4))).thenReturn(messagesPage);

        Page<Message>  messagesResult = service.findMessagesByChatId(chatId, userId, Pageable.ofSize(4));

        Assertions.assertEquals(messagesPage.getContent(), messagesResult.getContent());
        verify(repository).findById(chatId);
        verify(messageService).findMessagesByChatId(chatId, Pageable.ofSize(4));
    }

    @Test
    void testFindMessagesByChatId_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(1).getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        var pageable = Pageable.ofSize(4);
        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.findMessagesByChatId(chatId, userId, pageable)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }

    @Test
    void testFindMessagesByChatId_ifChatNotExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();

        when(repository.findById(chatId)).thenReturn(Optional.empty());

        var pageable = Pageable.ofSize(4);
        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.findMessagesByChatId(chatId, userId, pageable)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }


    @Test
    void testFindChatRoomsMessageByUserId() {
        String userId = users.get(0);
        var chatRoom = chatRooms.get(0);
        var messageFromChatRoom = chatRoom.getMessages().iterator().next();
        var value = new ChatRoomsMessageDto()
            .toBuilder()
            .chatRoomId(chatRoom.getId())
            .userId(userId)
            .messageId(messageFromChatRoom.getId())
            .text(messageFromChatRoom.getText())
            .build();
        Page<ChatRoomsMessageDto> expectedResult = new PageImpl<>(List.of(value));

        var pageable = Pageable.ofSize(4);
        when(repository.findChatRoomsMessageByUserId(userId, Pageable.ofSize(4))).thenReturn(expectedResult);

        Page<ChatRoomsMessageDto> chatRoomsMessageDtos = service.findChatRoomsMessageByUserId(userId, pageable);

        assertEquals(chatRoomsMessageDtos, expectedResult);
        verify(repository).findChatRoomsMessageByUserId(userId, pageable);
    }


    @Test
    void testCreateChatRoom_ifChatRoomNotExistsAndUserExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        ChatRoom expectChatRoom = chatRooms.get(0);

        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(false);
        when(restTemplate.exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class)).thenReturn(getSuccessResponse(true));
        when(repository.save(any(ChatRoom.class))).thenReturn(expectChatRoom);

        ChatRoom chatRoomResult = service.createChatRoom(dto);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class);
        verify(repository).save(any(ChatRoom.class));
    }

    @Test
    void testCreateChatRoom_ifChatRoomNotExistsAndUserNotExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();

        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(false);
        when(restTemplate.exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class)).thenReturn(getSuccessResponse(false));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.createChatRoom(dto)
            );

        Assertions.assertEquals(ErrorCodeException.USER_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(urlAuth + users.get(1), HttpMethod.GET, null, Boolean.class);
    }

    @Test
    void testCreateChatRoom_ifExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(true);

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.createChatRoom(dto)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_WITH_THESE_USERS_ALREADY_EXISTS, thrown.getErrorCodeException());
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
    }


    @Test
    void testDeleteChatRoom_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoomDeleteDto dto = ChatRoomDeleteDto.builder()
            .currentUserId(userId)
            .chatId(chatId)
            .build();
        ChatRoom chatRoomExpect = chatRooms.get(0);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        boolean result = service.deleteChatRoom(dto);

        Assertions.assertTrue(result);
        verify(repository).findById(chatId);
    }

    @Test
    void testDeleteChatRoom_ifChatExistsAndUserNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoomDeleteDto dto = ChatRoomDeleteDto.builder()
            .currentUserId(userId)
            .chatId(chatId)
            .build();
        ChatRoom chatRoomExpect = chatRooms.get(1);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteChatRoom(dto)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }

    @Test
    void testDeleteChatRoom_ifChatNotExistsAndUserNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoomDeleteDto dto = ChatRoomDeleteDto.builder()
            .currentUserId(userId)
            .chatId(chatId)
            .build();

        when(repository.findById(chatId)).thenReturn(Optional.empty());

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteChatRoom(dto)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }


    @Test
    void testSendMessage_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRoomExpect.getId())
            .currentUserId(userId)
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

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));
        when(messageService.sendMessage(messageCreateDto)).thenReturn(messageExpect);

        Message messageResult = service.sendMessage(messageCreateDto);

        Assertions.assertEquals(messageResult, messageExpect);
        verify(repository).findById(chatId);
        verify(messageService).sendMessage(messageCreateDto);
    }

    @Test
    void testSendMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(1).getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRoomExpect.getId())
            .currentUserId(userId)
            .text("some text")
            .build();

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.sendMessage(messageCreateDto)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }

    @Test
    void testSendMessage_ifChatNotExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(1).getId();
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRooms.get(1).getId())
            .currentUserId(userId)
            .text("some text")
            .build();

        when(repository.findById(chatId)).thenReturn(Optional.empty());

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.sendMessage(messageCreateDto)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }


    @Test
    void testDeleteMessage_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String messageId = chatRooms.get(0).getMessages().iterator().next().getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(messageId)
            .build();

        when(repository.findChatRoomByMessageId(messageId)).thenReturn(Optional.of(chatRoomExpect));
        doNothing().when(messageService).deleteMessage(dto);

        service.deleteMessage(dto);

        verify(repository).findChatRoomByMessageId(messageId);
        verify(messageService).deleteMessage(dto);
    }

    @Test
    void testDeleteMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String messageId = chatRooms.get(0).getMessages().iterator().next().getId();
        ChatRoom chatRoomExpect = chatRooms.get(1);
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(messageId)
            .build();

        when(repository.findChatRoomByMessageId(messageId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteMessage(dto)
        );

        assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(messageId);
    }

    @Test
    void testDeleteMessage_ifChatNotExists() {
        String userId = users.get(0);
        String messageId = chatRooms.get(0).getMessages().iterator().next().getId();
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(messageId)
            .build();

        when(repository.findChatRoomByMessageId(messageId)).thenReturn(Optional.empty());

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteMessage(dto)
        );

        assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(messageId);
    }


    @Test
    void testUpdateMessage_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageExpect = message
            .toBuilder()
            .text("some new text")
            .build();
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .text("some new text")
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.updateMessage(dto)).thenReturn(messageExpect);

        Message messageResult = service.updateMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).updateMessage(dto);
    }

    @Test
    void testUpdateMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        ChatRoom chatRoomFound = chatRooms.get(1);
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .text("some new text")
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.updateMessage(dto)
        );

        assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(message.getId());
    }

    @Test
    void testUpdateMessage_ifChatNotExists() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .text("some new text")
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.empty());

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.updateMessage(dto)
        );

        assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(message.getId());
    }


    @Test
    void testToggleLikeMessage_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageExpect = message
            .toBuilder()
            .messageLikes(Set.of())
            .build();
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .isLike(false)
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.toggleLikeMessage(dto)).thenReturn(messageExpect);

        Message messageResult = service.toggleLikeMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).toggleLikeMessage(dto);
    }

    @Test
    void testToggleLikeMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        ChatRoom chatRoomFound = chatRooms.get(1);
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .isLike(false)
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.toggleLikeMessage(dto)
        );

        assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(message.getId());
    }

    @Test
    void testToggleLikeMessage_ifChatNotExists() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .isLike(false)
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.empty());

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.toggleLikeMessage(dto)
        );

        assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(message.getId());
    }


    @Test
    void testReadLikeMessage_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageExpect = message
            .toBuilder()
            .messageReads(Set.of(users.get(1)))
            .build();
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.readMessage(dto)).thenReturn(messageExpect);

        Message messageResult = service.readMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).readMessage(dto);
    }

    @Test
    void testReadLikeMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        ChatRoom chatRoomFound = chatRooms.get(1);
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.readMessage(dto)
        );

        assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(message.getId());
    }

    @Test
    void testReadLikeMessage_ifChatNotExists() {
        String userId = users.get(0);
        Message message = chatRooms.get(0).getMessages().iterator().next();
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(message.getId())
            .build();

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.empty());

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.readMessage(dto)
        );

        assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findChatRoomByMessageId(message.getId());
    }


    private ResponseEntity<Boolean> getSuccessResponse(boolean value) {
        return new ResponseEntity<>(value, HttpStatus.OK);
    }
}
