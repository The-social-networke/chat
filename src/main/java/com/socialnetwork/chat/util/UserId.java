package com.socialnetwork.chat.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(value="request", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class UserId {
    private String id;
}
