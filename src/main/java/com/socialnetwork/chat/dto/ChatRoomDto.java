package com.socialnetwork.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class ChatRoomDto {

    private String id;

    private Set<String> users;

    protected LocalDateTime createdAt;
}
