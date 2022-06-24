package com.socialnetwork.chat.model.response;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Tag(name = "", description = "dto to info about chatRoom")
public class ChatRoomInfoRequest {

    private String id;

    private Set<String> users;

    private LocalDateTime createdAt;

    private Integer amountOfNotReadMessages;
}
