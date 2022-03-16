package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "message")
@Builder(toBuilder = true, builderClassName = "MessageBuilder")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message implements Serializable {

    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    private String text;

    private Byte[] photo;

    @Column(name = "content_id")
    private String contentId;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_updated")
    private boolean isUpdated;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private ChatRoom chatRoom;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="read_message", joinColumns=@JoinColumn(name="message_id"))
    @Column(name="user_id")
    private Set<String> messageReads;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="liked_message", joinColumns=@JoinColumn(name="message_id"))
    @Column(name="user_id")
    private Set<String> messageLikes;
}
