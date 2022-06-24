package com.socialnetwork.chat.model.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.socialnetwork.chat.model.enums.ForwardType;
import com.socialnetwork.chat.model.enums.MessageStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class MessageRequest {

    private String id;

    private String userId;

    private String text;

    private Byte[] photo;

    private String forwardId;

    private ForwardType forwardType;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sentAt;

    private boolean isUpdated;

    private Set<String> messageReads;

    private Set<String> messageLikes;

    private MessageStatus messageStatus;
}
