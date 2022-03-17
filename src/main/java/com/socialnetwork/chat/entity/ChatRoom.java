package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@NoArgsConstructor
@Table(name = "chat_room")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom implements Serializable {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user__chat_room", joinColumns=@JoinColumn(name="chat_room_id"))
    @Column(name="user_id", nullable = false)
    private Set<String> users;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy="chatRoom")
    private Set<Message> messages;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
}
