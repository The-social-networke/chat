package com.socialnetwork.chat.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_room")
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom {

    @Id
    @Column(updatable = false)
    private String id;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<ChatRoomUser> users;

    @OneToMany(mappedBy="chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Message> messages;

    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
}
