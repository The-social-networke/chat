package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
import com.socialnetwork.chat.repository.query.ChatRoomQuery;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "chat_room")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NamedNativeQuery(
    name = "ChatRoom.findChatRoomsMessage",
    query = ChatRoomQuery.FIND_CHAT_ROOMS_MESSAGE,
    resultSetMapping = "ChatRoomsMessageDto")
@NamedNativeQuery(
    name = "ChatRoom.findChatRoomsMessage.count",
    query = ChatRoomQuery.FIND_CHAT_ROOMS_MESSAGE_COUNT)
@SqlResultSetMapping(name = "ChatRoomsMessageDto",
    classes = {
        @ConstructorResult(
            targetClass = ChatRoomsMessageDto.class,
            columns = {
                @ColumnResult(name = "chatRoomId", type = String.class),
                @ColumnResult(name = "anotherUserId", type = String.class),
                @ColumnResult(name = "userId", type = String.class),
                @ColumnResult(name = "messageId", type = String.class),
                @ColumnResult(name = "text", type = String.class),
                @ColumnResult(name = "sentAt", type = LocalDateTime.class),
                @ColumnResult(name = "amountOfNotReadMessages", type = Integer.class)}
        )
    }
)
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = -4587740129089262748L;
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user__chat_room", joinColumns=@JoinColumn(name="chat_room_id"))
    @Column(name="user_id", nullable = false)
    private Set<String> users;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy="chatRoom")
    private Set<Message> messages;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
}
