package com.socialnetwork.chat.config;

import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.util.UserId;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class TokenHandlerFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private UserId userIdSet;

    @Value("${app.auth.url-get-id-by-toke}")
    private String url;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //get token from
        String header = request.getHeader("Authorization");
        //check if token exists
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ChatException(ErrorCodeException.FORBIDDEN);
        }
        //get token
        String authToken = header.substring(7);
        //set token to header request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        //get user id from auth service
        RestTemplate restTemplate = new RestTemplate();
        String userId = null;
        try {
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            userId = result.getBody();
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            resolver.resolveException(request, response, null, new ChatException(ErrorCodeException.FORBIDDEN));
            return;
        }

        //save id in session
        userIdSet.setId(userId);

        filterChain.doFilter(request, response);
    }
}
