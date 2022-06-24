package com.socialnetwork.chat.model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class ChatRoomResponse {

    private String id;

    private Set<String> users;

    protected LocalDateTime createdAt;
}
