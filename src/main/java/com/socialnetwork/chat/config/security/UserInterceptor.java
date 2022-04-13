package com.socialnetwork.chat.config.security;

import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.util.AuthModuleUtil;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class UserInterceptor implements ChannelInterceptor {

    private final String url;

    public UserInterceptor(String url) {
        this.url = url;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message<?> preSend(
        @NonNull Message<?> message,
        @NonNull MessageChannel channel
    ) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                var headers = (Map<String, List<String>>) message.getHeaders().get(NativeMessageHeaderAccessor.NATIVE_HEADERS);
                if(headers == null || !headers.containsKey("Authorization")) {
                    throw new ChatException(ErrorCodeException.UNAUTHORIZED);
                }
                List<String> auth = headers.get("Authorization");
                String userIdFromToken = AuthModuleUtil.getUserIdFromToken(auth.get(0), url, new RestTemplate());
                UserSecurity userSecurity = new UserSecurity(userIdFromToken);
                UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(userSecurity, null, null);
                accessor.setUser(authReq);
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
                throw new ChatException(ErrorCodeException.FORBIDDEN);
            }
        }
        return message;
    }
}
