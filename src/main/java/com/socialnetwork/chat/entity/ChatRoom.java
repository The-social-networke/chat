package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.dto.ChatRoomsMessageDto;
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
    name = "ChatRoomsMessageDtoSql",
    query = "SELECT chat.id chatRoomId, message.user_id userId, message.id messageId, message.text as text, message.sent_at sentAt, readed_cout.amountOfNotReadMessages" +
        " FROM chat_room chat" +
        "         JOIN user__chat_room user_chat" +
        "              ON chat.id = user_chat.chat_room_id" +
        "                  AND user_chat.user_id = :userId" +
        "         JOIN (" +
        "            SELECT DISTINCT ON (chat_room_id) chat_room_id, id, text, user_id, sent_at FROM" +
        "            (SELECT * FROM message" +
        "            ORDER BY message.chat_room_id, sent_at DESC) ordered_message" +
        "         ) AS message" +
        "            ON message.chat_room_id = chat.id" +
        "        JOIN (" +
        "            SELECT message.chat_room_id, COUNT(*) as amountOfNotReadMessages" +
        "            FROM message" +
        "                JOIN user__chat_room" +
        "                    ON user__chat_room.user_id = :userId" +
        "                        AND user__chat_room.chat_room_id = message.chat_room_id" +
        "                FULL JOIN read_message" +
        "                    ON read_message.message_id = message.id" +
        "                        AND read_message.user_id IS NULL" +
        "            GROUP BY message.chat_room_id" +
        "            ) as readed_cout" +
        "                ON readed_cout.chat_room_id = message.chat_room_id" +
        " ORDER BY message.sent_at DESC",
    resultSetMapping = "ChatRoomsMessageDto")
@SqlResultSetMapping(name = "ChatRoomsMessageDto",
    classes = {
        @ConstructorResult(
            targetClass = ChatRoomsMessageDto.class,
            columns = {
                @ColumnResult(name = "chatRoomId", type = String.class),
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
