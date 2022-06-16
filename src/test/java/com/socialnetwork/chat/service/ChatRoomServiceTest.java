package com.socialnetwork.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.chat.TestUtils;
import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.*;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.mapper.ChatRoomMapper;
import com.socialnetwork.chat.mapper.MessageMapper;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import com.socialnetwork.chat.service.impl.MessageService;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import com.socialnetwork.chat.util.enums.MessageStatus;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository repository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChatRoomServiceImpl service;

    @Captor
    ArgumentCaptor<ChatRoom> chatRoomCaptor;
    
    private List<String> users;

    private List<ChatRoom> chatRooms;

    private List<Message> messages;

    @BeforeEach
    void setUp() {
        TestUtils.setFieldsFromPropertiesFile(service);

        users = List.of(
            "ff2ce835-e775-40d2-a1b6-4e382b8e465c",
            "d3c5c1a2-f69b-49f6-93ea-68e29adce0d1",
            "027f8c23-8922-457d-b29b-6cb33b59684c"
        );
        messages = List.of(
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
                .build(),
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
                .build(),
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
                .build(),
            new Message()
                .toBuilder()
                .text("message 2 from system user")
                .userId(TestUtils.SYSTEM_USER_ID)
                .sentAt(LocalDateTime.now())
                .chatRoom(
                    new ChatRoom()
                        .toBuilder()
                        .id("19706be4-8499-4ddc-ab77-3w8321djc005")
                        .build()
                )
                .build()
        );

        messages.get(0).getMessageLikes().add(
          new MessageLikes(new MessageUserPk(users.get(1), messages.get(0).getId()), messages.get(0))
        );
        messages.get(1).getMessageLikes().add(
            new MessageLikes(new MessageUserPk(users.get(2), messages.get(1).getId()), messages.get(1))
        );
        messages.get(2).getMessageLikes().add(
            new MessageLikes(new MessageUserPk(users.get(0), messages.get(2).getId()), messages.get(2))
        );
        messages.get(3).getMessageLikes().add(
            new MessageLikes(new MessageUserPk(users.get(1), messages.get(3).getId()), messages.get(3))
        );
        messages.get(4).getMessageLikes().add(
            new MessageLikes(new MessageUserPk(users.get(2), messages.get(4).getId()), messages.get(4))
        );
        messages.get(5).getMessageLikes().add(
            new MessageLikes(new MessageUserPk(users.get(0), messages.get(5).getId()), messages.get(5))
        );

        chatRooms = List.of(
            new ChatRoom()
                .toBuilder()
                .id("288f7ad6-7d88-4a1e-8c5c-ebfcdcc58847")
                .createdAt(LocalDateTime.now())
                .messages(Set.of(
                    messages.get(0),
                    messages.get(1)
                ))
                .build(),
            new ChatRoom()
                .toBuilder()
                .id("09706be4-8462-4ddc-be31-5a8321fdc485")
                .createdAt(LocalDateTime.now())
                .messages(Set.of(
                    messages.get(2),
                    messages.get(3)
                ))
                .build(),
            new ChatRoom()
                .toBuilder()
                .id("19706be4-8499-4ddc-ab77-3w8321djc005")
                .createdAt(LocalDateTime.now())
                .messages(Set.of(
                    messages.get(4),
                    messages.get(5)
                ))
                .build()
        );

        chatRooms.get(0).setUsers(
            new HashSet<>(
                Lists.newArrayList(
                    new ChatRoomUser(new ChatRoomUserPk(users.get(0), chatRooms.get(0).getId()), chatRooms.get(0)),
                    new ChatRoomUser(new ChatRoomUserPk(users.get(1), chatRooms.get(0).getId()), chatRooms.get(0))
                )
            )
        );

        chatRooms.get(1).setUsers(
            new HashSet<>(
                Lists.newArrayList(
                    new ChatRoomUser(new ChatRoomUserPk(users.get(1), chatRooms.get(1).getId()), chatRooms.get(1)),
                    new ChatRoomUser(new ChatRoomUserPk(users.get(2), chatRooms.get(1).getId()), chatRooms.get(1))
                    )
            )
        );

        chatRooms.get(2).setUsers(
            new HashSet<>(
                Lists.newArrayList(
                    new ChatRoomUser(new ChatRoomUserPk(users.get(1), chatRooms.get(2).getId()), chatRooms.get(2)),
                    new ChatRoomUser(new ChatRoomUserPk(TestUtils.SYSTEM_USER_ID, chatRooms.get(2).getId()), chatRooms.get(2))
                    )
            )
        );
    }


    @Test
    void testGetChatRoomById_ifChatExistsAndUserIsMemberOfChat() {
        String chatId = chatRooms.get(0).getId();
        String userId = users.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message lastMessageInChatRoom = messages.get(0);
        ChatRoomMessageDto chatRoomMessageDtoExpect = new ChatRoomMessageDto()
            .toBuilder()
            .chatRoomId(chatRoomFound.getId())
            .anotherUserId(users.get(1))
            .sentAt(lastMessageInChatRoom.getSentAt())
            .text(lastMessageInChatRoom.getText())
            .userId(lastMessageInChatRoom.getUserId())
            .amountOfNotReadMessages(1L)
            .build();

        String anotherUserId = users.get(1);
        String url = TestUtils.BASE_URL + TestUtils.GET_INFO_BY_USER_ID_ENDPOINT + anotherUserId;

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomFound));
        when(restTemplate.getForObject(url, String.class))
            .thenReturn(null);
        when(repository.getChatRoomMessageByUserIdAndChatId(userId, chatId)).thenReturn(chatRoomMessageDtoExpect);

        ChatRoomMessageDto chatRoomMessageDtoResult = service.getChatRoomById(userId, chatId);

        Assertions.assertEquals(chatRoomMessageDtoResult, chatRoomMessageDtoExpect);
        verify(repository).findById(chatId);
        verify(restTemplate).getForObject(url, String.class);
    }

    @Test
    void testGetChatRoomById_ifChatExistsAndUserIsNotMemberOfChat() {
        String chatId = chatRooms.get(0).getId();
        String userId = users.get(2);
        ChatRoom chatRoomExpect = chatRooms.get(0);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.getChatRoomById(userId, chatId)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }

    @Test
    void testGetChatRoomById_ifChatNotExists() {
        String chatId = chatRooms.get(0).getId();
        String userId = users.get(0);
        ChatRoom chatRoomExpect = chatRooms.get(1);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.getChatRoomById(userId, chatId)
        );

        Assertions.assertEquals(ErrorCodeException.NOT_MEMBER_OF_CHAT, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }


    @Test
    void testGetChatRoomByUsersOrElseCreate_ifChatRoomIsExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        ChatRoom foundChatRoom = chatRooms.get(0);
        ChatRoomInfoDto expectChatRoom = new ChatRoomInfoDto()
            .toBuilder()
            .id(foundChatRoom.getId())
            .users(
                foundChatRoom.getUsers()
                    .stream()
                    .map(u -> u.getChatRoomUserPk().getUserId())
                    .collect(Collectors.toUnmodifiableSet())
            )
            .createdAt(foundChatRoom.getCreatedAt())
            .amountOfNotReadMessages(0)
            .build();

        when(repository.findChatRoomByUsers(users.get(0), users.get(1))).thenReturn(Optional.of(foundChatRoom));
        when(repository.getAmountOfNotReadMessages(foundChatRoom.getId())).thenReturn(0);

        ChatRoomInfoDto chatRoomResult = service.getChatRoomByUsersOrElseCreate(dto);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).findChatRoomByUsers(users.get(0), users.get(1));
        verify(repository).getAmountOfNotReadMessages(foundChatRoom.getId());
    }

    @Test
    void testGetChatRoomByUsersOrElseCreate_ifTwoUsersTheSame() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(0))
            .build();

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.getChatRoomByUsersOrElseCreate(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_CREATE_CHAT_WITH_HIMSELF, thrown.getErrorCodeException());
    }

    @Test
    void testGetChatRoomByUsersOrElseCreate_ifChatRoomIsNotExistAndUserExists() throws Exception {
        ChatRoom foundChatRoom = chatRooms.get(0);
        ChatRoomInfoDto expectChatRoom = new ChatRoomInfoDto()
            .toBuilder()
            .id(foundChatRoom.getId())
            .users(
                foundChatRoom.getUsers()
                    .stream()
                    .map(u -> u.getChatRoomUserPk().getUserId())
                    .collect(Collectors.toSet())
            )
            .createdAt(foundChatRoom.getCreatedAt())
            .amountOfNotReadMessages(0)
            .build();

        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        
        when(repository.findChatRoomByUsers(users.get(0), users.get(1))).thenReturn(Optional.empty());
        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class)).thenReturn(TestUtils.getResponseEntityBoolean(true));
        when(repository.save(any(ChatRoom.class))).thenReturn(foundChatRoom);

        ChatRoomInfoDto chatRoomResult = service.getChatRoomByUsersOrElseCreate(dto);

        Assertions.assertEquals(expectChatRoom, chatRoomResult);
        verify(repository).findChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class);
        verify(repository).save(chatRoomCaptor.capture());
        
        ChatRoom chatRoomForSave = chatRoomCaptor.getValue();
        boolean isContainUsers = chatRoomForSave.getUsers().stream()
            .filter(obj -> obj.getChatRoomUserPk().getUserId().equals(users.get(0))
                || obj.getChatRoomUserPk().getUserId().equals(users.get(1))).count() == 2;

        Assertions.assertTrue(isContainUsers);
        Assertions.assertNull(chatRoomForSave.getCreatedAt());
        Assertions.assertNull(chatRoomForSave.getMessages());
    }

    @Test
    void testGetChatRoomByUsersOrElseCreate_ifChatRoomIsNotExistAndUserNotExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();

        when(repository.findChatRoomByUsers(users.get(0), users.get(1))).thenReturn(Optional.empty());
        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class))
            .thenReturn(TestUtils.getResponseEntityBoolean(false));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.getChatRoomByUsersOrElseCreate(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class);
    }


    @Test
    void testGetSystemChatRoomByUsersOrElseCreate_ifChatRoomIsExists() {
        String userId = users.get(0);
        ChatRoom chatRoomFound = chatRooms.get(2);
        ChatRoomDto chatRoomExpect = ChatRoomMapper.toChatRoomDto(chatRoomFound);

        when(repository.findChatRoomByUsers(userId, TestUtils.SYSTEM_USER_ID)).thenReturn(Optional.of(chatRoomFound));

        ChatRoomDto chatRoomResult = service.getSystemChatRoomByUserOrElseCreate(userId);

        Assertions.assertEquals(chatRoomExpect, chatRoomResult);
        verify(repository).findChatRoomByUsers(userId, TestUtils.SYSTEM_USER_ID);
    }

    @Test
    void testGetSystemChatRoomByUsersOrElseCreate_ifChatRoomIsNotExistAndUserExists() {
        String userId = users.get(1);
        ChatRoom chatRoomSave = chatRooms.get(2);
        ChatRoomDto chatRoomExpect = ChatRoomMapper.toChatRoomDto(chatRoomSave);

        when(repository.findChatRoomByUsers(userId, TestUtils.SYSTEM_USER_ID)).thenReturn(Optional.empty());
        when(repository.save(any(ChatRoom.class))).thenReturn(chatRoomSave);

        ChatRoomDto chatRoomResult = service.getSystemChatRoomByUserOrElseCreate(userId);

        Assertions.assertEquals(chatRoomExpect, chatRoomResult);
        verify(repository).findChatRoomByUsers(userId, TestUtils.SYSTEM_USER_ID);
        verify(repository).save(chatRoomCaptor.capture());

        ChatRoom chatRoomForSave = chatRoomCaptor.getValue();
        boolean isContainUsers = chatRoomForSave.getUsers().stream()
            .filter(obj -> obj.getChatRoomUserPk().getUserId().equals(TestUtils.SYSTEM_USER_ID)
                || obj.getChatRoomUserPk().getUserId().equals(users.get(1))).count() == 2;

        Assertions.assertTrue(isContainUsers);
        Assertions.assertNull(chatRoomForSave.getCreatedAt());
        Assertions.assertNull(chatRoomForSave.getMessages());
    }


    @Test
    void textGetAmountOfAllNotReadMessages_thereAreNoNotReadMessages() {
        String userId = users.get(0);

        when(repository.getAmountOfAllNotReadMessages(userId)).thenReturn(0);

        Integer result = service.getAmountOfAllNotReadMessages(userId);

        Assertions.assertEquals(0, result);
        verify(repository).getAmountOfAllNotReadMessages(userId);
    }

    @Test
    void textGetAmountOfAllNotReadMessages_thereAreNotReadMessage() {
        String userId = users.get(0);

        when(repository.getAmountOfAllNotReadMessages(userId)).thenReturn(10);

        Integer result = service.getAmountOfAllNotReadMessages(userId);

        Assertions.assertEquals(10, result);
        verify(repository).getAmountOfAllNotReadMessages(userId);
    }


    @Test
    void testFindMessagesByChatId_ifChatExistsAndUserIsMemberOfChat() {
        String userId = users.get(0);
        String chatId = chatRooms.get(0).getId();
        ChatRoom chatRoomExpect = chatRooms.get(0);
        Page<Message> messagesFound = new PageImpl<>(new ArrayList<>(chatRooms.get(0).getMessages()));
        Page<MessageDto> messagesExpect = messagesFound.map(MessageMapper::toMessageDto);

        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomExpect));
        when(messageService.findMessagesByChatId(chatId, Pageable.ofSize(4))).thenReturn(messagesFound);

        Page<MessageDto>  messagesResult = service.findMessagesByChatId(userId, chatId, Pageable.ofSize(4));

        Assertions.assertEquals(messagesExpect.getContent(), messagesResult.getContent());
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
            () -> service.findMessagesByChatId(userId, chatId, pageable)
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
            () -> service.findMessagesByChatId(userId, chatId, pageable)
        );

        Assertions.assertEquals(ErrorCodeException.CHAT_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).findById(chatId);
    }


    @Test
    void testFindChatRoomsMessageByUserId() {
        String userId = users.get(0);
        String anotherUserId = users.get(1);
        var chatRoom = chatRooms.get(0);
        var messageFromChatRoom = messages.get(0);
        var value = new ChatRoomMessageDto()
            .toBuilder()
            .anotherUserId(anotherUserId)
            .sentAt(LocalDateTime.now())
            .amountOfNotReadMessages(0L)
            .chatRoomId(chatRoom.getId())
            .userId(userId)
            .messageId(messageFromChatRoom.getId())
            .text(messageFromChatRoom.getText())
            .build();
        Page<ChatRoomMessageDto> expectedResult = new PageImpl<>(List.of(value));

        String url = TestUtils.BASE_URL + TestUtils.GET_INFO_BY_USER_ID_ENDPOINT + anotherUserId;

        var pageable = Pageable.ofSize(4);
        when(repository.findChatRoomsMessageByUserId(userId, Pageable.ofSize(4))).thenReturn(expectedResult);
        when(restTemplate.getForObject(url, String.class))
            .thenReturn("[]");

        Page<ChatRoomMessageDto> chatRoomsMessageDtos = service.findChatRoomsMessageByUserId(userId, pageable);

        assertEquals(chatRoomsMessageDtos, expectedResult);
        verify(repository).findChatRoomsMessageByUserId(userId, pageable);
        verify(restTemplate).getForObject(url, String.class);
    }


    @Test
    void testCreateChatRoom_ifChatRoomNotExistsAndUserExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();
        ChatRoom chatRoomSave = chatRooms.get(0);
        ChatRoomDto chatRoomExpect = ChatRoomMapper.toChatRoomDto(chatRoomSave);

        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(false);
        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class))
            .thenReturn(TestUtils.getResponseEntityBoolean(true));
        when(repository.save(any(ChatRoom.class))).thenReturn(chatRoomSave);

        ChatRoomDto chatRoomResult = service.createChatRoom(dto);

        Assertions.assertEquals(chatRoomExpect, chatRoomResult);
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class);
        verify(repository).save(chatRoomCaptor.capture());

        ChatRoom chatRoomForSave = chatRoomCaptor.getValue();
        boolean isContainUsers = chatRoomForSave.getUsers().stream()
            .filter(obj -> obj.getChatRoomUserPk().getUserId().equals(users.get(0))
                || obj.getChatRoomUserPk().getUserId().equals(users.get(1))).count() == 2;

        Assertions.assertTrue(isContainUsers);
        Assertions.assertNull(chatRoomForSave.getCreatedAt());
        Assertions.assertNull(chatRoomForSave.getMessages());
    }

    @Test
    void testCreateChatRoom_ifUsersTheSame() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(0))
            .build();

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.createChatRoom(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_CREATE_CHAT_WITH_HIMSELF, thrown.getErrorCodeException());
    }

    @Test
    void testCreateChatRoom_ifChatRoomNotExistsAndUserNotExists() {
        ChatRoomCreateDto dto = new ChatRoomCreateDto()
            .toBuilder()
            .currentUserId(users.get(0))
            .userId(users.get(1))
            .build();

        when(repository.existsChatRoomByUsers(users.get(0), users.get(1))).thenReturn(false);
        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class))
            .thenReturn(TestUtils.getResponseEntityBoolean(false));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.createChatRoom(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_NOT_FOUND, thrown.getErrorCodeException());
        verify(repository).existsChatRoomByUsers(users.get(0), users.get(1));
        verify(restTemplate).exchange(TestUtils.getUrlToCheckIfUserExists(users.get(1)), HttpMethod.GET, null, Boolean.class);
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
        ChatRoom chatRoomFound = chatRooms.get(0);
        MessageCreateDto messageCreateDto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId(chatRoomFound.getId())
            .currentUserId(userId)
            .text("some text")
            .build();
        Message messageSaved = new Message()
            .toBuilder()
            .id("3bb05f77-2b8f-4bf6-969d-8179cb298e69")
            .sentAt(LocalDateTime.now())
            .isUpdated(false)
            .chatRoom(chatRoomFound)
            .userId(userId)
            .text("some text")
            .messageStatus(MessageStatus.SENT)
            .build();
        var chatRoomsMessageStatusExpect = TestUtils.convertToChatRoomsMessageStatusDto(chatRoomFound.getId(), messageSaved);
        MessageDto messageExpect = MessageMapper.toMessageDto(messageSaved);


        when(repository.findById(chatId)).thenReturn(Optional.of(chatRoomFound));
        when(messageService.sendMessage(messageCreateDto)).thenReturn(messageSaved);
        doNothing().when(template).convertAndSend("/users/" + users.get(1), chatRoomsMessageStatusExpect);
        doNothing().when(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), messageSaved);

        MessageDto messageResult = service.sendMessage(messageCreateDto);

        Assertions.assertEquals(messageExpect, messageResult);
        verify(repository).findById(chatId);
        verify(messageService).sendMessage(messageCreateDto);
        verify(template).convertAndSend("/users/" + users.get(1), chatRoomsMessageStatusExpect);
        verify(template).convertAndSend("/chat/messages/" + messageCreateDto.getChatRoomId(), MessageMapper.toMessageDto(messageSaved));
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
    void testDeleteMessage_ifChatExistsAndUserIsMemberOfChat_notLastMessage() {
        String userId = users.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageDeleted = messages.get(0)
            .toBuilder()
            .messageStatus(MessageStatus.DELETED)
            .build();
        String messageId = messageDeleted.getId();
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(messageId)
            .build();
        MessageDto messageDtoSend = MessageMapper.toMessageDto(messageDeleted);
        MessageDto messageExpect = MessageMapper.toMessageDto(messageDeleted);


        when(repository.findChatRoomByMessageId(messageId)).thenReturn(Optional.of(chatRoomFound));
        when(repository.isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId())).thenReturn(false);
        when(messageService.deleteMessage(dto)).thenReturn(messageDeleted);
        doNothing().when(template).convertAndSend(eq("/users/" + users.get(1)), any(Object.class));
        doNothing().when(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), messageDtoSend);

        MessageDto messageResult = service.deleteMessage(dto);


        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(messageId);
        verify(repository).isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId());
        verify(messageService).deleteMessage(dto);
        verify(template, never()).convertAndSend(eq("/users/" + users.get(1)), any(Object.class));
        verify(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), messageDtoSend);
    }

    @Test
    void testDeleteMessage_ifChatExistsAndUserIsMemberOfChat_lastMessageAndHasOtherMessage() {
        String userId = users.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageDeleted = messages.get(0)
            .toBuilder()
            .messageStatus(MessageStatus.DELETED)
            .build();
        String messageId = messageDeleted.getId();
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(messageId)
            .build();
        MessageDto messageExpect = MessageMapper.toMessageDto(messageDeleted);

        Message lastMessage = messages.get(1)
            .toBuilder()
            .messageStatus(MessageStatus.DELETED)
            .build();
        var chatRoomsMessageStatusExpect = TestUtils.convertToChatRoomsMessageStatusDto(chatRoomFound.getId(), lastMessage);

        when(repository.findChatRoomByMessageId(messageId)).thenReturn(Optional.of(chatRoomFound));
        when(repository.isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId())).thenReturn(true);
        when(messageService.deleteMessage(dto)).thenReturn(messageDeleted);
        when(messageRepository.findFirstByChatRoomIdOrderBySentAtDesc(chatRoomFound.getId())).thenReturn(Optional.of(messages.get(1)));
        doNothing().when(template).convertAndSend("/users/" + users.get(1), chatRoomsMessageStatusExpect);
        doNothing().when(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageDeleted));

        MessageDto messageResult = service.deleteMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(messageId);
        verify(repository).isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId());
        verify(messageService).deleteMessage(dto);
        verify(messageRepository).findFirstByChatRoomIdOrderBySentAtDesc(chatRoomFound.getId());
        verify(template).convertAndSend("/users/" + users.get(1), chatRoomsMessageStatusExpect);
        verify(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageDeleted));
    }

    @Test
    void testDeleteMessage_ifChatExistsAndUserIsMemberOfChat_lastMessageAndNotHasOtherMessage() {
        String userId = users.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageDeleted = messages.get(0)
            .toBuilder()
            .messageStatus(MessageStatus.DELETED)
            .build();
        String messageId = messageDeleted.getId();
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(messageId)
            .build();
        var chatRoomsMessageStatusExpect = TestUtils.convertToChatRoomsMessageStatusDto(chatRoomFound.getId(),
            new Message()
                .toBuilder()
                .chatRoom(chatRoomFound)
                .userId(dto.getCurrentUserId())
                .messageStatus(MessageStatus.DELETED)
                .build()
        );
        MessageDto messageExpect = MessageMapper.toMessageDto(messageDeleted);

        when(repository.findChatRoomByMessageId(messageId)).thenReturn(Optional.of(chatRoomFound));
        when(repository.isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId())).thenReturn(true);
        when(messageService.deleteMessage(dto)).thenReturn(messageDeleted);
        when(messageRepository.findFirstByChatRoomIdOrderBySentAtDesc(chatRoomFound.getId())).thenReturn(Optional.empty());
        doNothing().when(template).convertAndSend("/users/" + users.get(1), chatRoomsMessageStatusExpect);
        doNothing().when(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageDeleted));

        MessageDto messageResult = service.deleteMessage(dto);


        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(messageId);
        verify(repository).isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId());
        verify(messageService).deleteMessage(dto);
        verify(messageRepository).findFirstByChatRoomIdOrderBySentAtDesc(chatRoomFound.getId());
        verify(template).convertAndSend("/users/" + users.get(1), chatRoomsMessageStatusExpect);
        verify(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageDeleted));
    }

    @Test
    void testDeleteMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        String messageId = messages.get(0).getId();
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
        String messageId = messages.get(0).getId();
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
    void testUpdateMessage_ifChatExistsAndUserIsMemberOfChat_notLastMessage() {
        String userId = users.get(0);
        Message message = messages.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageSaved = message
            .toBuilder()
            .text("some new text")
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .text("some new text")
            .currentUserId(userId)
            .messageId(message.getId())
            .build();
        MessageDto messageExpect = MessageMapper.toMessageDto(messageSaved);

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.updateMessage(dto)).thenReturn(messageSaved);
        doNothing().when(template).convertAndSend(eq("/users/" + users.get(1)), any(Object.class));
        doNothing().when(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), messageSaved);


        MessageDto messageResult = service.updateMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).updateMessage(dto);
        verify(template, never()).convertAndSend(eq("/users/" + users.get(1)), any(Object.class));
        verify(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageSaved));
    }

    @Test
    void testUpdateMessage_ifChatExistsAndUserIsMemberOfChat_lastMessage() {
        String userId = users.get(0);
        Message message = messages.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageSaved = message
            .toBuilder()
            .text("some new text")
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .text("some new text")
            .currentUserId(userId)
            .messageId(message.getId())
            .build();
        var chatRoomMessageDto = TestUtils.convertToChatRoomsMessageStatusDto(chatRoomFound.getId(), messageSaved);
        MessageDto messageExpect = MessageMapper.toMessageDto(messageSaved);

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.updateMessage(dto)).thenReturn(messageSaved);
        when(repository.isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId())).thenReturn(true);
        doNothing().when(template).convertAndSend("/users/" + users.get(1), chatRoomMessageDto);
        doNothing().when(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageSaved));


        MessageDto messageResult = service.updateMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).updateMessage(dto);
        verify(repository).isLastMessageInChatRoom(chatRoomFound.getId(), dto.getMessageId());
        verify(template).convertAndSend("/users/" + users.get(1), chatRoomMessageDto);
        verify(template).convertAndSend("/chat/messages/" + chatRoomFound.getId(), MessageMapper.toMessageDto(messageSaved));
    }

    @Test
    void testUpdateMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        Message message = messages.get(0);
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
        Message message = messages.get(0);
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
        Message message = messages.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageSaved = message
            .toBuilder()
            .messageLikes(new HashSet<>())
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .isLike(false)
            .currentUserId(userId)
            .messageId(message.getId())
            .build();
        MessageDto messageExpect = MessageMapper.toMessageDto(messageSaved);

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.toggleLikeMessage(dto)).thenReturn(messageSaved);

        MessageDto messageResult = service.toggleLikeMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).toggleLikeMessage(dto);
    }

    @Test
    void testToggleLikeMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        Message message = messages.get(0);
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
        Message message = messages.get(0);
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
        Message message = messages.get(0);
        ChatRoom chatRoomFound = chatRooms.get(0);
        Message messageSaved = message
            .toBuilder()
            .messageReads(new HashSet<>(Lists.newArrayList(
                new MessageReaders(new MessageUserPk(userId, messages.get(1).getId()), message)
            )))
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .currentUserId(userId)
            .messageId(message.getId())
            .build();
        MessageDto messageExpect = MessageMapper.toMessageDto(messageSaved);

        when(repository.findChatRoomByMessageId(message.getId())).thenReturn(Optional.of(chatRoomFound));
        when(messageService.readMessage(dto)).thenReturn(messageSaved);

        MessageDto messageResult = service.readMessage(dto);

        assertEquals(messageExpect, messageResult);
        verify(repository).findChatRoomByMessageId(message.getId());
        verify(messageService).readMessage(dto);
    }

    @Test
    void testReadLikeMessage_ifChatExistsAndUserIsNotMemberOfChat() {
        String userId = users.get(0);
        Message message = messages.get(0);
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
        Message message = messages.get(0);
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
}
