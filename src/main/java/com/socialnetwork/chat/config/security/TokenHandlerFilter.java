package com.socialnetwork.chat.config.security;

import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.util.AuthModuleUtil;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
@Order(1)
@Component
public class TokenHandlerFilter extends OncePerRequestFilter {

    @Value("${app.auth.url}")
    private String url;

    @Value("${app.auth.not-auth-endpoints}")
    private String[] allowedNotAuthEndpoints;

    private final HandlerExceptionResolver resolver;

    private final RestTemplate restTemplate;


    @Autowired
    public TokenHandlerFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                              RestTemplate restTemplate) {
        this.resolver = resolver;
        this.restTemplate = restTemplate;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId;
        try {
            //get token from
            String header = request.getHeader("Authorization");
            //check if token exists
            if (header == null || !header.startsWith("Bearer ")) {
                throw new ChatException(ErrorCodeException.UNAUTHORIZED);
            }
            //get chat from auth service
            userId = AuthModuleUtil.getUserIdFromToken(header, url, restTemplate);
        }
        catch (ChatException ex) {
            log.error(ex.getMessage());
            resolver.resolveException(request, response, null, new ChatException(ex.getErrorCodeException()));
            return;
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            resolver.resolveException(request, response, null, new ChatException(ErrorCodeException.FORBIDDEN));
            return;
        }

        //set principal
        UserSecurity userSecurity = new UserSecurity(userId);
        UsernamePasswordAuthenticationToken authReq
            = new UsernamePasswordAuthenticationToken(userSecurity, null, null);
        SecurityContextHolder.getContext().setAuthentication(authReq);
        authReq.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        log.info("yes auth");
        doFilter(request, response, filterChain);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String[] inauthenticationEndpoints = allowedNotAuthEndpoints;
        return Stream.of(inauthenticationEndpoints).anyMatch(path::matches);
    }
}
