package com.socialnetwork.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomCreateDto {

    @NotEmpty(message = "user should be present")
    private String userId;

    @JsonIgnore
    private String currentUserId;
}
