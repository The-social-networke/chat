package com.socialnetwork.chat.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ApiModel(description = "dto to info about chatRoom")
public class ChatRoomInfoDto {

    private String id;

    private Set<String> users;

    private LocalDateTime createdAt;

    private Integer amountOfNotReadMessages;
}
