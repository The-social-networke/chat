package com.socialnetwork.chat.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class JwtTokenUtil {

    public static String getUserIdFromToken(String bearToken, String url) {
        //get token
        String authToken = bearToken.substring(7);
        //set token to header request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        //get user id from auth service
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return result.getBody();
    }
}
