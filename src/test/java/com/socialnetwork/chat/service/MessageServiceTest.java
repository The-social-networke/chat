package com.socialnetwork.chat.service;

import com.socialnetwork.chat.dto.*;
import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.mapper.MessageMapper;
import com.socialnetwork.chat.mapper.MessageMapperImpl;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.impl.MessageService;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
@RunWith(MockitoJUnitRunner.class)
class MessageServiceTest {

    @Mock
    private MessageRepository repository;

    @Spy
    private MessageMapper mapper = new MessageMapperImpl();

    @InjectMocks
    private MessageService service;

    private String chatId;

    private List<Message> messages;

    @BeforeEach
    void setUp() {
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
    void testFindAllByChatId() {
        Page<Message> messagesPage = new PageImpl<>(messages);

        when(repository.findAllByChatRoomId(eq(chatId), any(Pageable.class))).thenReturn(messagesPage);

        Page<Message> messagesResult = service.findMessagesByChatId(chatId, Pageable.ofSize(4));

        Assertions.assertEquals(messagesResult.getContent(), messagesPage.getContent());
        verify(repository).findAllByChatRoomId(eq(chatId), any(Pageable.class));
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
    void testDeleteMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(0);
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .currentUserId(currentUser)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));
        doNothing().when(repository).delete(expectFoundMessage);

        service.deleteMessage(dto);

        verify(repository).findById(dto.getMessageId());
        verify(repository).delete(expectFoundMessage);
    }

    @Test
    void testDeleteMessage_ifMessageNotBelongToUser() {
        String currentUser = messages.get(1).getUserId();
        Message expectFoundMessage = messages.get(0);
        MessageDeleteDto dto = new MessageDeleteDto()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .currentUserId(currentUser)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteMessage(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_DELETE_NOT_OWN_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
    }


    @Test
    void testReadMessage_ifMessageNotBelongToUserAndNotReadBefore() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(1);
        Message expectSavedMessage = messages.get(1)
            .toBuilder()
            .messageReads(Set.of(currentUser))
            .build();
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .currentUserId(currentUser)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));
        when(repository.save(expectFoundMessage)).thenReturn(expectSavedMessage);

        Message returnedMessage = service.readMessage(dto);

        Assertions.assertEquals(returnedMessage, expectSavedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(expectFoundMessage);
    }

    @Test
    void testReadMessage_ifMessageNotBelongToUserAndReadBefore() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(1)
            .toBuilder()
            .messageReads(Set.of(currentUser))
            .build();
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .currentUserId(currentUser)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));

        Message returnedMessage = service.readMessage(dto);

        Assertions.assertEquals(returnedMessage, expectFoundMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }

    @Test
    void testReadMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(0);
        MessageReadDto dto = new MessageReadDto()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .currentUserId(currentUser)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.readMessage(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_READ_HIS_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
    }


    @Test
    void testUpdateMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(0);
        Message expectSavedMessage = messages.get(0)
            .toBuilder()
            .isUpdated(true)
            .text("some new text")
            .build();
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .currentUserId(currentUser)
            .text("some new text")
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));
        when(repository.save(expectSavedMessage)).thenReturn(expectSavedMessage);

        Message message = service.updateMessage(dto);

        Assertions.assertEquals(expectSavedMessage, message);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(expectSavedMessage);
    }

    @Test
    void testUpdateMessage_ifMessageNotBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(1);
        MessageUpdateDto dto = new MessageUpdateDto()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .currentUserId(currentUser)
            .text("some new text")
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));


        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.updateMessage(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_UPDATE_NOT_OWN_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }


    @Test
    void testLikeMessage_ifMessageNotBelongToUserAndLikeToggleToTrue() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(1);
        Message expectSavedMessage = messages.get(1)
            .toBuilder()
            .messageLikes(Set.of(currentUser))
            .build();
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .currentUserId(currentUser)
            .isLike(true)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));
        when(repository.save(expectSavedMessage)).thenReturn(expectSavedMessage);

        Message returnedMessage = service.toggleLikeMessage(dto);

        Assertions.assertEquals(expectSavedMessage, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(expectSavedMessage);
    }

    @Test
    void testLikeMessage_ifMessageNotBelongToUserAndLikeToggleToFalse() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(1)
            .toBuilder()
            .messageLikes(new HashSet<>() {{
                add(currentUser);
            }})
            .build();
        Message expectSavedMessage = messages.get(1);
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .currentUserId(currentUser)
            .isLike(false)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));
        when(repository.save(expectSavedMessage)).thenReturn(expectSavedMessage);

        Message returnedMessage = service.toggleLikeMessage(dto);

        Assertions.assertEquals(expectSavedMessage, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(expectSavedMessage);
    }

    @Test
    void testLikeMessage_ifMessageNotBelongToUserAndLikeTheSame() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(1);
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .currentUserId(currentUser)
            .isLike(false)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));

        Message returnedMessage = service.toggleLikeMessage(dto);

        Assertions.assertEquals(expectFoundMessage, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }

    @Test
    void testLikeMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message expectFoundMessage = messages.get(0);
        MessageLikeDto dto = new MessageLikeDto()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .currentUserId(currentUser)
            .isLike(true)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(expectFoundMessage));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.toggleLikeMessage(dto)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_LIKE_HIS_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }
}
