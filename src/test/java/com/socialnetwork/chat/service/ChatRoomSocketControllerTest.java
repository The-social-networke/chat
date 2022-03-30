package com.socialnetwork.chat.service;


import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.MessageCreateDto;
import com.socialnetwork.chat.entity.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ChatRoomSocketControllerTest {

    static final String URL = "ws://localhost:";
    static final String CONNECT_SOCKET_ENDPOINT = "/ws-chat";
    static final String WEBSOCKET_TOPIC = "/chat/messages/";

    private CompletableFuture<Message> completableFuture;

    @LocalServerPort
    private Integer port;

    @Mock
    private WebSocketStompClient webSocketStompClient;

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        UserSecurity userSecurity = new UserSecurity("1234");
        UsernamePasswordAuthenticationToken authReq
            = new UsernamePasswordAuthenticationToken(userSecurity, null, null);

        SecurityContextHolder.getContext().setAuthentication(authReq);
    }

    @Test
    public void shouldReceiveAMessageFromTheServer() throws Exception {
        String chatId = "1234";
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(HttpHeaders.EMPTY);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBnbWFpbC5jb20iLCJpYXQiOjE2NDg1NDcwNjMsImV4cCI6MTY0ODU1MDY2M30.-uWnoDKLxljkgxFpa1PCbqjoapqUptjFmQHKFzWKXEI");
        StompSession session = webSocketStompClient
            .connect(URL + port + CONNECT_SOCKET_ENDPOINT, headers, stompHeaders, new StompSessionHandlerAdapter() {})
            .get(5000, SECONDS);
        session.subscribe("/chat/messages/" + chatId, new MessageStompFrameHandler());
        var obj = new MessageCreateDto()
            .toBuilder()
            .text("1234")
            .chatRoomId("1234")
            .build();
        //todo check it https://rieckpil.de/write-integration-tests-for-your-spring-websocket-endpoints/
        session.send("/app/chat/sendMessage/", "obj");
    }

    public class MessageStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object obj) {
            System.out.println("Received message: " + obj);
            completableFuture.complete((Message) obj);
        }
    }
}