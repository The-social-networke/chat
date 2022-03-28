package com.socialnetwork.chat.service;


import com.socialnetwork.chat.util.AuthModuleUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.anyString;


@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ChatRoomSocketController {

    static final String URL = "ws://localhost:";
    static final String CONNECT_SOCKET_ENDPOINT = "/ws-chat";
    static final String WEBSOCKET_TOPIC = "/chat/messages/";

    @LocalServerPort
    private Integer port;



    @Mock
    private WebSocketStompClient webSocketStompClient;


    @Before
    public void setup() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    public void shouldReceiveAMessageFromTheServer() throws Exception {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(HttpHeaders.EMPTY);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbDJAZ21haWwuY29tIiwiaWF0IjoxNjQ4NDE2MDMyLCJleHAiOjE2NDg0MTk2MzJ9.T74O0y2LhVi7Je3noYEWU1-gNouM8_BCLu2ZAHYY6Xw");
        StompSession session = webSocketStompClient
            .connect(URL + port + CONNECT_SOCKET_ENDPOINT, headers, stompHeaders, new StompSessionHandlerAdapter() {})
            .get(5000, SECONDS);
    }
}
