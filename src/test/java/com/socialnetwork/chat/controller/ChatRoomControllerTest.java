package com.socialnetwork.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.chat.TestUtils;
import com.socialnetwork.chat.dto.ChatRoomCreateDto;
import com.socialnetwork.chat.dto.ChatRoomDeleteDto;
import com.socialnetwork.chat.dto.ChatRoomMessageDto;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.repository.ChatRoomRepository;
import com.socialnetwork.chat.repository.MessageRepository;
import com.socialnetwork.chat.service.ChatRoomService;
import com.socialnetwork.chat.service.impl.ChatRoomServiceImpl;
import com.socialnetwork.chat.service.impl.MessageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(value = {"classpath:setup-test-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:clear-test-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ChatRoomControllerTest {

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
        ChatRoomService chatRoomService = spy(new ChatRoomServiceImpl(messageRepository, chatRoomRepository, messageService, simpMessagingTemplate, restTemplate, objectMapper));
        ChatRoomController controller = spy(new ChatRoomController(chatRoomService));
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(new TestUtils.PrincipalDetailsArgumentResolver(authorizedUserId))
            .setControllerAdvice(new ControllerAdvice())
            .build();

        TestUtils.setFieldsFromPropertiesFile(chatRoomService);
    }


    @Test
    void testGetChatRoomById_success() throws Exception {
        String anotherUserId = "55ab96d7-8a93-4ea3-9d9d-77500018ad4e";
        var chatId = "350c19d5-2905-4c6e-9e60-4bb74a53745e";
        mockMvc.perform(get("/chat")
                .param("chatId", chatId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chatRoomId").value(chatId))
            .andExpect(jsonPath("$.anotherUserId").value("55ab96d7-8a93-4ea3-9d9d-77500018ad4e"))
            .andExpect(jsonPath("$.userId").value("55ab96d7-8a93-4ea3-9d9d-77500018ad4e"))
            .andExpect(jsonPath("$.messageId").value("50f43dd9-35e4-4c00-bd3a-c7b26575b153"))
            .andExpect(jsonPath("$.text").value("great!"))
            .andExpect(jsonPath("$.sentAt").isNotEmpty())
            .andExpect(jsonPath("$.amountOfNotReadMessages").value(1))
            .andExpect(jsonPath("$.userInfo", Matchers.nullValue()))
            .andExpect(jsonPath("$.*", hasSize(8)));
    }

    @Test
    void testGetChatRoomById_unsuccess() throws Exception {
        var chatId = "cf35f7d8-2672-4b16-9457-9a4206e65930";

        mockMvc.perform(get("/chat")
                .param("chatId", chatId))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("not member of chat"))
            .andExpect(jsonPath("$.errorCode").value(1002))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }


    @Test
    void testCreateChatRoom_success() throws Exception {
        String anotherUserId = "88e6b54b-dcbd-4d3d-a633-f013fadbe25b";
        var dto = new ChatRoomCreateDto()
            .toBuilder()
            .userId(anotherUserId)
            .build();

        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(anotherUserId), HttpMethod.GET, null, Boolean.class))
            .thenReturn(TestUtils.getResponseEntityBoolean(true));

        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.users", hasSize(2)))
            .andExpect(jsonPath("$.users", hasItem(anotherUserId)))
            .andExpect(jsonPath("$.users", hasItem(authorizedUserId)))
            .andExpect(jsonPath("$.*", hasSize(3)));

        verify(restTemplate).exchange(TestUtils.getUrlToCheckIfUserExists(anotherUserId), HttpMethod.GET, null, Boolean.class);
    }

    @Test
    void testCreateChatRoom_unsuccess() throws Exception {
        String anotherUserId = "55ab96d7-8a93-4ea3-9d9d-77500018ad4e";
        var dto = new ChatRoomCreateDto()
            .toBuilder()
            .userId(anotherUserId)
            .build();

        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(anotherUserId), HttpMethod.GET, null, Boolean.class))
            .thenReturn(TestUtils.getResponseEntityBoolean(true));

        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat with these users already exits"))
            .andExpect(jsonPath("$.errorCode").value(1003))
            .andExpect(jsonPath("$.*", hasSize(2)));

        verify(restTemplate, never()).exchange(TestUtils.getUrlToCheckIfUserExists(anotherUserId), HttpMethod.GET, null, Boolean.class);
    }


    @Test
    void testFindChatRoomsMessage_success() throws Exception {
        var messagesExpect = new ChatRoomMessageDto[]{
            new ChatRoomMessageDto()
                .toBuilder()
                .messageId("69be2df7-0c33-4112-8ea7-e226f9fb1887")
                .chatRoomId("3157333e-d7b2-4735-9069-fbd2cbf8e9f1")
                .userId("f510077c-144d-4ade-bec6-6c8fd6913544")
                .text("test message")
                .sentAt(LocalDateTime.of(2022, 4, 1, 20, 20, 7, 220500))
                .amountOfNotReadMessages(1L)
                .build(),
            new ChatRoomMessageDto()
                .toBuilder()
                .messageId("50f43dd9-35e4-4c00-bd3a-c7b26575b153")
                .chatRoomId("350c19d5-2905-4c6e-9e60-4bb74a53745e")
                .userId("55ab96d7-8a93-4ea3-9d9d-77500018ad4e")
                .text("great!")
                .sentAt(LocalDateTime.of(2022, 3, 23, 10, 10, 7, 220500))
                .amountOfNotReadMessages(1L)
                .build(),
        };

        mockMvc.perform(get("/chat/find-chats"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].messageId").value(messagesExpect[0].getMessageId()))
            .andExpect(jsonPath("$.content[0].chatRoomId").value(messagesExpect[0].getChatRoomId()))
            .andExpect(jsonPath("$.content[0].userId").value(messagesExpect[0].getUserId()))
            .andExpect(jsonPath("$.content[0].text").value(messagesExpect[0].getText()))
            .andExpect(jsonPath("$.content[0].sentAt").isNotEmpty())
            .andExpect(jsonPath("$.content[0].amountOfNotReadMessages").value(messagesExpect[0].getAmountOfNotReadMessages()))
            .andExpect(jsonPath("$.content[1].messageId").value(messagesExpect[1].getMessageId()))
            .andExpect(jsonPath("$.content[1].chatRoomId").value(messagesExpect[1].getChatRoomId()))
            .andExpect(jsonPath("$.content[1].userId").value(messagesExpect[1].getUserId()))
            .andExpect(jsonPath("$.content[1].text").value(messagesExpect[1].getText()))
            .andExpect(jsonPath("$.content[1].sentAt").isNotEmpty())
            .andExpect(jsonPath("$.content[1].amountOfNotReadMessages").value(messagesExpect[1].getAmountOfNotReadMessages()));
    }


    @Test
    void testGetChatRoomByUsersOrElseCreate_success() throws Exception {
        var dto = new ChatRoomCreateDto()
            .toBuilder()
            .userId("55ab96d7-8a93-4ea3-9d9d-77500018ad4e")
            .build();
        mockMvc.perform(post("/chat/get-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void testGetChatRoomByUsersOrElseCreate_unsuccess() throws Exception {
        var anotherUserId = "91091319-77de-4985-a7fc-37db9b828493";
        var dto = new ChatRoomCreateDto()
            .toBuilder()
            .userId(anotherUserId)
            .build();

        when(restTemplate.exchange(TestUtils.getUrlToCheckIfUserExists(anotherUserId), HttpMethod.GET, null, Boolean.class))
            .thenReturn(TestUtils.getResponseEntityBoolean(false));

        mockMvc.perform(post("/chat/get-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(jsonPath("$.message").value("user not found"))
            .andExpect(jsonPath("$.errorCode").value(1000))
            .andExpect(jsonPath("$.*", hasSize(2)));

        verify(restTemplate).exchange(TestUtils.getUrlToCheckIfUserExists(anotherUserId), HttpMethod.GET, null, Boolean.class);
    }


    @Test
    void testGetSystemChatRoomByUserOrElseCreate_success() throws Exception {
        mockMvc.perform(post("/chat/get-system-chat"))
            .andDo(print())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.users", hasItem("38bcd488-2d2b-4f50-976b-cae650f6a3f0")))
            .andExpect(jsonPath("$.users", hasItem("8a744b81-38fd-4fe1-a032-33836e7a0221")))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.*", hasSize(3)));
    }


    @Test
    void testDeleteChatRoom_success() throws Exception {
        var dto = new ChatRoomDeleteDto()
            .toBuilder()
            .chatId("350c19d5-2905-4c6e-9e60-4bb74a53745e")
            .build();

        mockMvc.perform(delete("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testDeleteChatRoom_unsuccess() throws Exception {
        var dto = new ChatRoomDeleteDto()
            .toBuilder()
            .chatId("d438bcc2-7622-4c18-96ea-2b250082ecbb")
            .build();

        mockMvc.perform(delete("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }


    @Test
    void testFindAllMessageByChatRoomId_success() throws Exception {
        var chatId = "3157333e-d7b2-4735-9069-fbd2cbf8e9f1";
        var messageExpected = new Message()
            .toBuilder()
            .id("69be2df7-0c33-4112-8ea7-e226f9fb1887")
            .userId("f510077c-144d-4ade-bec6-6c8fd6913544")
            .text("test message")
            //.userId()
            .build();
        mockMvc.perform(get("/chat/all-messages")
                .param("chatId", chatId))
            .andDo(print())
            .andExpect(jsonPath("$.content[0].id").value(messageExpected.getId()))
            .andExpect(jsonPath("$.content[0].userId").value(messageExpected.getUserId()))
            .andExpect(jsonPath("$.content[0].text").value(messageExpected.getText()))
            .andExpect(jsonPath("$.content[0].sentAt", Matchers.notNullValue()))
            .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    void testFindAllMessageByChatRoomId_unsuccess() throws Exception {
        var chatId = "6c1f2878-6152-4315-b0a1-26e3d2244de8";
        mockMvc.perform(get("/chat/all-messages")
                .param("chatId", chatId))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("chat not found"))
            .andExpect(jsonPath("$.errorCode").value(1001))
            .andExpect(jsonPath("$.*", hasSize(2)));
    }
}
