package com.socialnetwork.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.chat.TestUtils;
import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.dto.MessageDeleteDto;
import com.socialnetwork.chat.dto.MessageLikeDto;
import com.socialnetwork.chat.dto.MessageUpdateDto;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import com.socialnetwork.chat.service.impl.MessageService;
import com.socialnetwork.chat.util.enums.MessageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(value = {"classpath:setup-test-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:clear-test-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ChatRoomSocketControllerTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private RestTemplate restTemplate;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String authorizedUserId = "8a744b81-38fd-4fe1-a032-33836e7a0221";

    @BeforeEach
    void setUp() {
        ChatRoomService chatRoomService = spy(new ChatRoomServiceImpl(messageRepository, chatRoomRepository, messageService, simpMessagingTemplate, restTemplate));
        ChatRoomSocketController controller = spy(new ChatRoomSocketController(chatRoomService));
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(new TestUtils.PrincipalDetailsArgumentResolver(authorizedUserId))
            .setControllerAdvice(new ControllerAdvice())
            .build();

        TestUtils.setFieldsFromPropertiesFile(chatRoomService);
    }


    @Test
    void testSendMessage_success() throws Exception {
        var dto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId("350c19d5-2905-4c6e-9e60-4bb74a53745e")
            .text("some new mesage")
            .build();
        var messageExpect = new Message()
            .toBuilder()
            .userId("8a744b81-38fd-4fe1-a032-33836e7a0221")
            .text("some new mesage")
            .messageStatus(MessageStatus.SENT)
            .build();

        mockMvc.perform(post("/chat/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.userId").value(messageExpect.getUserId()))
            .andExpect(jsonPath("$.text").value(messageExpect.getText()))
            .andExpect(jsonPath("$.messageStatus").value(messageExpect.getMessageStatus().toString()))
            .andExpect(jsonPath("$.sentAt").isNotEmpty());
    }

    @Test
    void testSendMessage_unsuccess() throws Exception {
        var dto = new MessageCreateDto()
            .toBuilder()
            .chatRoomId("350c19d5-2905-4c6e-9e60-4bb74a537556")
            .text("some new mesage")
            .build();

        mockMvc.perform(post("/chat/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }


    @Test
    void testDeleteMessage_success() throws Exception {
        var dto = new MessageDeleteDto()
            .toBuilder()
            .messageId("7c610c79-369c-42af-9d51-bb3bc0891065")
            .build();
        var messageExpect = new Message()
            .toBuilder()
            .id("7c610c79-369c-42af-9d51-bb3bc0891065")
            .userId("8a744b81-38fd-4fe1-a032-33836e7a0221")
            .text("how are you")
            .messageStatus(MessageStatus.DELETED)
            .build();

        mockMvc.perform(delete("/chat/deleteMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageExpect.getId()))
            .andExpect(jsonPath("$.userId").value(messageExpect.getUserId()))
            .andExpect(jsonPath("$.text").value(messageExpect.getText()))
            .andExpect(jsonPath("$.messageStatus").value(messageExpect.getMessageStatus().toString()))
            .andExpect(jsonPath("$.sentAt").isNotEmpty());
    }

    @Test
    void testDeleteMessage_unsuccess() throws Exception {
        var dto = new MessageDeleteDto()
            .toBuilder()
            .messageId("7c610c79-369c-42af-9d51-bb3bc0891678")
            .build();

        mockMvc.perform(delete("/chat/deleteMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }


    @Test
    void testUpdateMessage_success() throws Exception {
        var dto = new MessageUpdateDto()
            .toBuilder()
            .messageId("7c610c79-369c-42af-9d51-bb3bc0891065")
            .text("how r u?")
            .build();
        var messageExpect = new Message()
            .toBuilder()
            .id("7c610c79-369c-42af-9d51-bb3bc0891065")
            .userId("8a744b81-38fd-4fe1-a032-33836e7a0221")
            .text("how r u?")
            .messageStatus(MessageStatus.UPDATED)
            .build();

        mockMvc.perform(post("/chat/updateMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageExpect.getId()))
            .andExpect(jsonPath("$.userId").value(messageExpect.getUserId()))
            .andExpect(jsonPath("$.text").value(messageExpect.getText()))
            .andExpect(jsonPath("$.messageStatus").value(messageExpect.getMessageStatus().toString()))
            .andExpect(jsonPath("$.sentAt").isNotEmpty());
    }

    @Test
    void testUpdateMessage_unsuccess() throws Exception {
        var dto = new MessageUpdateDto()
            .toBuilder()
            .messageId("7c610c79-369c-42af-9d51-bb3bc0891678")
            .text("how r u?")
            .build();

        mockMvc.perform(post("/chat/updateMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void testLikeMessage_success() throws Exception {
        var dto = new MessageLikeDto()
            .toBuilder()
            .messageId("50f43dd9-35e4-4c00-bd3a-c7b26575b153")
            .isLike(true)
            .build();
        var messageExpect = new Message()
            .toBuilder()
            .id("50f43dd9-35e4-4c00-bd3a-c7b26575b153")
            .userId("55ab96d7-8a93-4ea3-9d9d-77500018ad4e")
            .text("great!")
            .messageStatus(MessageStatus.UPDATED)
            .build();

        mockMvc.perform(post("/chat/likeMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageExpect.getId()))
            .andExpect(jsonPath("$.userId").value(messageExpect.getUserId()))
            .andExpect(jsonPath("$.text").value(messageExpect.getText()))
            .andExpect(jsonPath("$.messageStatus").value(messageExpect.getMessageStatus().toString()))
            .andExpect(jsonPath("$.sentAt").isNotEmpty())
            .andExpect(jsonPath("$.messageLikes", hasItem(authorizedUserId)));
    }

    @Test
    void testLikeMessage_unsuccess() throws Exception {
        var dto = new MessageLikeDto()
            .toBuilder()
            .messageId("50f43dd9-35e4-4c00-bd3a-c7b26575b123")
            .isLike(true)
            .build();

        mockMvc.perform(post("/chat/likeMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void testReadMessage_success() throws Exception {
        var dto = new MessageLikeDto()
            .toBuilder()
            .messageId("50f43dd9-35e4-4c00-bd3a-c7b26575b153")
            .isLike(true)
            .build();
        var messageExpect = new Message()
            .toBuilder()
            .id("50f43dd9-35e4-4c00-bd3a-c7b26575b153")
            .userId("55ab96d7-8a93-4ea3-9d9d-77500018ad4e")
            .text("great!")
            .messageStatus(MessageStatus.UPDATED)
            .build();

        mockMvc.perform(post("/chat/readMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(messageExpect.getId()))
            .andExpect(jsonPath("$.userId").value(messageExpect.getUserId()))
            .andExpect(jsonPath("$.text").value(messageExpect.getText()))
            .andExpect(jsonPath("$.messageStatus").value(messageExpect.getMessageStatus().toString()))
            .andExpect(jsonPath("$.sentAt").isNotEmpty())
            .andExpect(jsonPath("$.messageReads", hasItem(authorizedUserId)));
    }

    @Test
    void testReadMessage_unsuccess() throws Exception {
        var dto = new MessageLikeDto()
            .toBuilder()
            .messageId("50f43dd9-35e4-4c00-bd3a-c7b26575b123")
            .isLike(true)
            .build();

        mockMvc.perform(post("/chat/readMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }
}
