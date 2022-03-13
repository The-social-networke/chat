package com.socialnetwork.chat.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomCreateDto {

    @NotEmpty(message = "users should be present")
    private Set<String> users;
}
