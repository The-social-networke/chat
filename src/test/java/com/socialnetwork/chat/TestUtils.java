package com.socialnetwork.chat;

import com.socialnetwork.chat.config.security.UserSecurity;
import com.socialnetwork.chat.dto.ChatRoomMessageStatusDto;
import com.socialnetwork.chat.entity.Message;
import com.socialnetwork.chat.service.ChatRoomService;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class TestUtils {

    public static String BASE_URL = "http://198.211.110.141:3000";

    public static String IS_EXISTS_USER_BY_ID_ENDPOINT = "/user/exists-by-id?userId=";

    public static String USER_ID_BY_TOKEN_ENDPOINT = "/user/id";

    public static String GET_INFO_BY_USER_ID_ENDPOINT = "/user/get_info_by_user_id?userId=";

    public static String SYSTEM_USER_ID = "38bcd488-2d2b-4f50-976b-cae650f6a3f0";


    public static void setFieldsFromPropertiesFile(ChatRoomService service) {
        ReflectionTestUtils.setField(service, "url", BASE_URL);
        ReflectionTestUtils.setField(service, "systemUserId", TestUtils.SYSTEM_USER_ID);
        ReflectionTestUtils.setField(service, "endpointGetInfoByUserId", TestUtils.GET_INFO_BY_USER_ID_ENDPOINT);
    }

    public static ChatRoomMessageStatusDto convertToChatRoomsMessageStatusDto(String chatRoomId, Message message) {
        return new ChatRoomMessageStatusDto()
            .toBuilder()
            .chatRoomId(chatRoomId)
            .messageId(message == null ? null : message.getId())
            .text(message == null ? null : message.getText())
            .sentAt(message == null ? null : message.getSentAt())
            .userId(message == null ? null : message.getUserId())
            .messageStatus(message == null ? null :  message.getMessageStatus())
            .build();
    }

    public static ResponseEntity<Boolean> getResponseEntityBoolean(boolean value) {
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    public static ResponseEntity<String> getResponseEntityText(String text) {
        return new ResponseEntity<>(text, HttpStatus.OK);
    }

    public static String getUrlToCheckIfUserExists(String userId) {
        return BASE_URL + IS_EXISTS_USER_BY_ID_ENDPOINT + userId;
    }


    public static class PrincipalDetailsArgumentResolver implements HandlerMethodArgumentResolver {

        private String authorizedUserId;

        public PrincipalDetailsArgumentResolver(String authorizedUserId) {
            this.authorizedUserId = authorizedUserId;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().isAssignableFrom(UserSecurity.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return new UserSecurity(authorizedUserId);
        }
    }
}
