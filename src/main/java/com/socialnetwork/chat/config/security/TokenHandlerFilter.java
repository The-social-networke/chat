package com.socialnetwork.chat.config.security;

import com.socialnetwork.chat.exception.ChatException;
import com.socialnetwork.chat.util.JwtTokenUtil;
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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Order(1)
@Component
public class TokenHandlerFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolver;

    @Value("${app.auth.url-get-id-by-toke}")
    private String url;


    @Autowired
    public TokenHandlerFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //allow endpoint for test
        String path = request.getRequestURI();

        if (!path.matches("/chat/.*")) {
            filterChain.doFilter(request, response);
            return;
        }
        //get token from
        String header = request.getHeader("Authorization");
        //check if token exists
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ChatException(ErrorCodeException.FORBIDDEN);
        }

        //get chat from auth service
        String userId;
        try {
            userId = JwtTokenUtil.getUserIdFromToken(header, url);
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

        doFilter(request, response, filterChain);
    }
}
