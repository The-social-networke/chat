package com.socialnetwork.chat.controller;


import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ChatRoomSocketControllerTest {

    static final String URL = "ws://localhost:";
    static final String CONNECT_SOCKET_ENDPOINT = "/ws-chat";
    static final String WEBSOCKET_TOPIC = "/chat/messages/";

    private ArrayBlockingQueue<Message> blockingQueue;

    @LocalServerPort
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    private ChatRoomSocketController controller;

    @Before
    public void setup() {
        blockingQueue = new ArrayBlockingQueue<>(1);
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void verifyGreetingIsReceived() throws Exception {
        String chatId = "b045d3de-2093-432a-b903-4e1d6fd6f539";
        String userId = "52d9f27d-32f7-4312-8a35-4c8d2e0cb49a";

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(HttpHeaders.EMPTY);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbDJAZ21haWwuY29tIiwiaWF0IjoxNjQ4NzU5OTcxLCJleHAiOjE2NDg3NjM1NzF9.P47k0TfzXluFIuIPwGUJ1X3IqRLnR93OrxYwZTo9u9s");

        StompSession session = webSocketStompClient
            .connect(URL + port + CONNECT_SOCKET_ENDPOINT, headers, stompHeaders, new StompSessionHandlerAdapter() {})
            .get(3, SECONDS);
        session.subscribe("/chat/messages/" + chatId, new DefaultStompFrameHandler());
        var obj = new MessageCreateDto()
            .toBuilder()
            .text("12312312")
            .chatRoomId(chatId)
            .build();
        //session.send("/app/chat/sendMessage/" + chatId, obj);
        when(controller.sendMessage(any(), any())).thenReturn(null);
        session.send("/app/chat/sendMessage/" + chatId, obj);
        assertEquals(new Message(),  blockingQueue.poll(1, SECONDS));
    }

    class DefaultStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            Message msg = (Message) payload;
            blockingQueue.add(msg);
        }
    }
}