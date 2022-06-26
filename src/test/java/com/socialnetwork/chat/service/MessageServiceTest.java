package com.socialnetwork.chat.service;

import com.socialnetwork.chat.entity.ChatRoom;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.entity.MessageLike;
import com.socialnetwork.chat.entity.MessageReaders;
import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.model.enums.ErrorCodeException;
import com.socialnetwork.chat.model.enums.MessageStatus;
import com.socialnetwork.chat.model.request.*;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.impl.MessageService;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class MessageServiceTest {

    @Mock
    private MessageRepository repository;

    @InjectMocks
    private MessageService service;

    @Captor
    private ArgumentCaptor<Message> captor;

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

        when(repository.findAllByChatRoomIdOrderBySentAtDesc(eq(chatId), any(Pageable.class))).thenReturn(messagesPage);

        Page<Message> messagesResult = service.findMessagesByChatId(chatId, Pageable.ofSize(4));

        Assertions.assertEquals(messagesResult.getContent(), messagesPage.getContent());
        verify(repository).findAllByChatRoomIdOrderBySentAtDesc(eq(chatId), any(Pageable.class));
    }


    @Test
    void testSentMessage_ifMessageTodayIsNotFirst() {
        String userId = "1";
        Message expectedResult = messages.get(0)
            .toBuilder()
            .messageStatus(MessageStatus.SENT)
            .build();
        MessageCreateRequest dto = new MessageCreateRequest()
            .toBuilder()
            .chatRoomId(chatId)
            .text(expectedResult.getText())
            .build();

        when(repository.getAmountOfMessagesInChatRoomByDate(eq(expectedResult.getChatRoom().getId()), any(LocalDate.class))).thenReturn(2);
        when(repository.save(any(Message.class))).thenReturn(expectedResult);

        Message messageResult = service.sendMessage(dto, userId);

        Assertions.assertEquals(expectedResult, messageResult);
        verify(repository).save(any(Message.class));
        verify(repository).getAmountOfMessagesInChatRoomByDate(eq(expectedResult.getChatRoom().getId()), any(LocalDate.class));
    }

    @Test
    void testSentMessage_ifFirst() {
        String userId = "1";
        Message expectedResult = messages.get(0)
            .toBuilder()
            .messageStatus(MessageStatus.SENT)
            .build();
        MessageCreateRequest dto = new MessageCreateRequest()
            .toBuilder()
            .chatRoomId(chatId)
            .text(expectedResult.getText())
            .build();

        when(repository.getAmountOfMessagesInChatRoomByDate(eq(expectedResult.getChatRoom().getId()), any(LocalDate.class))).thenReturn(0);
        when(repository.save(any(Message.class))).thenReturn(expectedResult);

        Message messageResult = service.sendMessage(dto, userId);

        Assertions.assertEquals(expectedResult, messageResult);
        verify(repository, times(2)).save(any(Message.class));
        verify(repository).getAmountOfMessagesInChatRoomByDate(eq(expectedResult.getChatRoom().getId()), any(LocalDate.class));
    }


    @Test
    void testDeleteMessage_ifMessageBelongToUserAndNotLastByDay() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(0);
        MessageDeleteRequest dto = new MessageDeleteRequest()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));
        when(repository.getAmountOfMessagesInChatRoomByDate(eq(messageFound.getChatRoom().getId()), any(LocalDate.class))).thenReturn(2);
        doNothing().when(repository).delete(messageFound);

        service.deleteMessage(dto, currentUser);

        verify(repository).findById(dto.getMessageId());
        verify(repository).delete(messageFound);
        verify(repository).getAmountOfMessagesInChatRoomByDate(eq(messageFound.getChatRoom().getId()), any(LocalDate.class));
    }

    @Test
    void testDeleteMessage_ifMessageBelongToUserAndLastByDay() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(0);
        MessageDeleteRequest dto = new MessageDeleteRequest()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));
        when(repository.getAmountOfMessagesInChatRoomByDate(eq(messageFound.getChatRoom().getId()), any(LocalDate.class))).thenReturn(1);
        doNothing().when(repository).deleteSystemMessageByDate(eq(messageFound.getChatRoom().getId()), any(LocalDate.class));
        doNothing().when(repository).delete(messageFound);

        service.deleteMessage(dto, currentUser);

        verify(repository).findById(dto.getMessageId());
        verify(repository).getAmountOfMessagesInChatRoomByDate(eq(messageFound.getChatRoom().getId()), any(LocalDate.class));
        verify(repository).deleteSystemMessageByDate(eq(messageFound.getChatRoom().getId()), any(LocalDate.class));
        verify(repository).delete(messageFound);
    }

    @Test
    void testDeleteMessage_ifMessageNotBelongToUser() {
        String currentUser = messages.get(1).getUserId();
        Message messageFound = messages.get(0);
        MessageDeleteRequest dto = new MessageDeleteRequest()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.deleteMessage(dto, currentUser)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_DELETE_NOT_OWN_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
    }


    @Test
    void testReadMessage_ifMessageNotBelongToUserAndNotReadBefore() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(1);
        Message messageSaved = messages.get(1)
            .toBuilder()
            .messageReads(new HashSet<>(Lists.newArrayList(
                new MessageReaders(currentUser, messages.get(1))
            )))
            .build();
        Message messageExpect = messageSaved.toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageReadRequest dto = new MessageReadRequest()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));
        when(repository.save(messageFound)).thenReturn(messageSaved);

        Message returnedMessage = service.readMessage(dto, currentUser);

        Assertions.assertEquals(messageExpect, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(messageFound);
    }

    @Test
    void testReadMessage_ifMessageNotBelongToUserAndReadBefore() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(1)
            .toBuilder()
            .messageReads(
                new HashSet<>(Lists.newArrayList(
                    new MessageReaders(currentUser, messages.get(1))
                )
            ))
            .build();
        MessageReadRequest dto = new MessageReadRequest()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));

        Message returnedMessage = service.readMessage(dto, currentUser);

        Assertions.assertEquals(returnedMessage, messageFound);
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }

    @Test
    void testReadMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(0);
        MessageReadRequest dto = new MessageReadRequest()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.readMessage(dto, currentUser)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_READ_HIS_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
    }


    @Test
    void testUpdateMessage_ifMessageNotBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(1);
        MessageUpdateRequest dto = new MessageUpdateRequest()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .text("some new text")
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));


        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.updateMessage(dto, currentUser)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_UPDATE_NOT_OWN_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }


    @Test
    void testUpdateMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(0);
        Message messageSaved = messages.get(0)
            .toBuilder()
            .isUpdated(true)
            .text("some new text")
            .build();
        Message expectExpectResult = messageSaved
            .toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageUpdateRequest dto = new MessageUpdateRequest()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .text("some new text")
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));
        when(repository.save(messageSaved)).thenReturn(messageSaved);

        Message messageResult = service.updateMessage(dto, currentUser);

        Assertions.assertEquals(expectExpectResult, messageResult);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(messageSaved);
    }


    @Test
    void testLikeMessage_ifMessageNotBelongToUserAndLikeToggleToTrue() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(1);
        Message messageSaved = messages.get(1)
            .toBuilder()
            .messageLikes( new HashSet<>(Lists.newArrayList(
                new MessageLike(currentUser, messageFound)
            )))
            .build();
        Message messageExpect = messageSaved.toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageLikeRequest dto = new MessageLikeRequest()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .isLike(true)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));
        when(repository.save(any())).thenReturn(messageSaved);

        Message returnedMessage = service.toggleLikeMessage(dto, currentUser);

        Assertions.assertEquals(messageExpect, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(messageSaved);
    }

    @Test
    void testLikeMessage_ifMessageNotBelongToUserAndLikeToggleToFalse() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(1)
            .toBuilder()
            .messageLikes(
                new HashSet<>(Lists.newArrayList(
                    new MessageLike(currentUser, messages.get(1))
                )
            ))
            .build();
        Message expectSavedMessage = messages.get(1);
        Message messageExpect = expectSavedMessage.toBuilder()
            .messageStatus(MessageStatus.UPDATED)
            .build();
        MessageLikeRequest dto = new MessageLikeRequest()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .isLike(false)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));
        when(repository.save(expectSavedMessage)).thenReturn(expectSavedMessage);

        Message returnedMessage = service.toggleLikeMessage(dto, currentUser);

        Assertions.assertEquals(messageExpect, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository).save(expectSavedMessage);
    }

    @Test
    void testLikeMessage_ifMessageNotBelongToUserAndLikeTheSame() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(1);
        MessageLikeRequest dto = new MessageLikeRequest()
            .toBuilder()
            .messageId(messages.get(1).getId())
            .isLike(false)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));

        Message returnedMessage = service.toggleLikeMessage(dto, currentUser);

        Assertions.assertEquals(messageFound, returnedMessage);
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }

    @Test
    void testLikeMessage_ifMessageBelongToUser() {
        String currentUser = messages.get(0).getUserId();
        Message messageFound = messages.get(0);
        MessageLikeRequest dto = new MessageLikeRequest()
            .toBuilder()
            .messageId(messages.get(0).getId())
            .isLike(true)
            .build();

        when(repository.findById(dto.getMessageId())).thenReturn(Optional.of(messageFound));

        ChatException thrown = assertThrows(
            ChatException.class,
            () -> service.toggleLikeMessage(dto, currentUser)
        );

        Assertions.assertEquals(ErrorCodeException.USER_CANNOT_LIKE_HIS_MESSAGE, thrown.getErrorCodeException());
        verify(repository).findById(dto.getMessageId());
        verify(repository, never()).save(any());
    }
}
