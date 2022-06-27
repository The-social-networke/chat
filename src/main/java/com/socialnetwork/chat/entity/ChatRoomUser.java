package com.socialnetwork.chat.entity;

import javax.persistence.*;

@Entity
@Table(name = "user__chat_room")
public class ChatRoomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ChatRoom chatRoom;

    public ChatRoomUser() {
    }

    public ChatRoomUser(String userId, ChatRoom chatRoom) {
        this.userId = userId;
        this.chatRoom = chatRoom;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }
}
