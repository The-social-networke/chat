package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.enums.ForwardType;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUpdateDto {

    private String text;

    @NotNull(message = "message id should not be null")
    private String messageId;

    @Null(message = "user id should be null")
    private String userId;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;
}
