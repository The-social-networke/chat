package com.socialnetwork.chat.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthModuleUtil {

    private static final String ENDPOINT_GET_TOKEN_BY_ID = "/user/by_token";
    private static final String ENDPOINT_EXISTS_USER_BY_ID = "/user/exists_by_id";


    public static String getUserIdFromToken(String bearToken, String url) throws RestClientException {
        //get token
        String authToken = bearToken.substring(7);
        //set token to header request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        //get user id from auth service
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.exchange(url + ENDPOINT_GET_TOKEN_BY_ID, HttpMethod.GET, entity, String.class);
        return result.getBody();
    }

    public static boolean existsUserById(String userId, String url) throws RestClientException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Boolean> result = restTemplate.exchange(url + ENDPOINT_EXISTS_USER_BY_ID + "?userId=" + userId, HttpMethod.GET, null, Boolean.class);
        return Boolean.TRUE.equals(result.getBody());
    }
}
