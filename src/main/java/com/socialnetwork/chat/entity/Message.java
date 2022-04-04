package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.socialnetwork.chat.util.enums.ForwardType;
import com.socialnetwork.chat.util.enums.MessageStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "message")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message implements Serializable {

    private static final long serialVersionUID = 4093469586290478383L;

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    private String text = "";

    private Byte[] photo = null;

    @Column(name = "forward_id")
    private String forwardId = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "forward_type")
    private ForwardType forwardType = null;

    @CreationTimestamp
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_updated", columnDefinition = "boolean default false")
    private boolean isUpdated = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ChatRoom chatRoom;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="read_message", joinColumns=@JoinColumn(name="message_id"))
    @Column(name="user_id")
    private Set<String> messageReads = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="liked_message", joinColumns=@JoinColumn(name="message_id"))
    @Column(name="user_id")
    private Set<String> messageLikes = new HashSet<>();

    @Transient
    private MessageStatus messageStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return isUpdated == message.isUpdated && Objects.equals(id, message.id) && Objects.equals(userId, message.userId) && Objects.equals(text, message.text) && Arrays.equals(photo, message.photo) && Objects.equals(forwardId, message.forwardId) && forwardType == message.forwardType && Objects.equals(sentAt, message.sentAt) && Objects.equals(chatRoom, message.chatRoom) && Objects.equals(messageReads, message.messageReads) && Objects.equals(messageLikes, message.messageLikes) && Objects.equals(messageStatus, message.messageStatus);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, userId, text, forwardId, forwardType, sentAt, isUpdated, chatRoom, messageReads, messageLikes, messageStatus);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }


}
