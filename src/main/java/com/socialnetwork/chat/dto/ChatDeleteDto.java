package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDeleteDto {

    @NotNull(message = "id should not be null")
    private String chatId;

    @JsonIgnore
    private String currentUserId;
}
