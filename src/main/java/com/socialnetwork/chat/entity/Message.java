package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialnetwork.chat.util.enums.ForwardType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message implements Serializable {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    private String text;

    private Byte[] photo;

    @Column(name = "forward_id")
    private String forwardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "forward_type")
    private ForwardType forwardType;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_updated", columnDefinition = "boolean default false")
    private Boolean isUpdated = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
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
