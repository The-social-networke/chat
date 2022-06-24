package com.socialnetwork.chat.controller;


import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.model.request.MessageCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//todo need to do it
class SocketTest {

    static final String URL = "ws://localhost:";
    static final String CONNECT_SOCKET_ENDPOINT = "/ws-chat";
    static final String SUBSCRIBE_CHAT_MESSAGES_ENDPOINT = "/chat/messages/";
    static final String SUBSCRIBE_USER_ENDPOINT = "/users/";

    @LocalServerPort
    private Integer port;

    private ArrayBlockingQueue<Message> blockingQueueMessage;

    private ArrayBlockingQueue<Throwable> blockingQueueErrors;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setup() {
        blockingQueueMessage = new ArrayBlockingQueue<>(1);
        blockingQueueErrors = new ArrayBlockingQueue<>(1);
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }


    void verifyGreetingIsReceived() throws Exception {
        String chatId = "b045d3de-2093-432a-b903-4e1d6fd6f539";
        String userId = "52d9f27d-32f7-4312-8a35-4c8d2e0cb49a";

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(HttpHeaders.EMPTY);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbDJAZ21haWwuY29tIiwiaWF0IjoxNjQ5MTE5NDI5LCJleHAiOjE2NDkxMjMwMjl9.cj5av7hQssDa76EVXRjwxU1bVpmgVTCYnOu30YccYfs");

        StompSession session = webSocketStompClient
            .connect(URL + port + CONNECT_SOCKET_ENDPOINT, headers, stompHeaders, new StompSessionHandlerAdapter() {})
            .get(3, SECONDS);
        var ww = session.subscribe(SUBSCRIBE_CHAT_MESSAGES_ENDPOINT + chatId, new DefaultStompFrameHandler());
        var obj = new MessageCreateRequest()
            .toBuilder()
            .text("12312312")
            .chatRoomId(chatId)
            .build();

        //var ww2 = session.send(SEND_MESSAGE_ENDPOINT + chatId, obj);
        //var aa = ContexHol
        var result = blockingQueueMessage.poll(1, SECONDS);
        var exception = blockingQueueErrors.poll(1,SECONDS);

        System.out.println();
    }

    class DefaultStompFrameHandler extends StompSessionHandlerAdapter implements StompFrameHandler {


        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            System.out.println();
        }
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            blockingQueueErrors.add(exception);
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            Message msg = (Message) payload;
            blockingQueueMessage.add(msg);
        }
    }
}