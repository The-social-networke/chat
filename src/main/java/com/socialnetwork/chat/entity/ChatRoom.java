package com.socialnetwork.chat.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "chat_room")
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

    public ChatRoom() {
    }

    private ChatRoom(String id, Set<ChatRoomUser> users, Set<Message> messages, LocalDateTime createdAt) {
        this.id = id;
        this.users = users;
        this.messages = messages;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<ChatRoomUser> getUsers() {
        return users;
    }

    public void setUsers(Set<ChatRoomUser> users) {
        this.users = users;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ChatRoom.ChatRoomBuilder builder() {
        return new ChatRoom.ChatRoomBuilder();
    }

    public ChatRoom.ChatRoomBuilder toBuilder() {
        return (new ChatRoom.ChatRoomBuilder()).id(this.id).users(this.users).messages(this.messages).createdAt(this.createdAt);
    }

    public static class ChatRoomBuilder {
        private String id;
        private Set<ChatRoomUser> users;
        private Set<Message> messages;
        private LocalDateTime createdAt;

        ChatRoomBuilder() {
        }

        public ChatRoom.ChatRoomBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ChatRoom.ChatRoomBuilder users(Set<ChatRoomUser> users) {
            this.users = users;
            return this;
        }

        public ChatRoom.ChatRoomBuilder messages(Set<Message> messages) {
            this.messages = messages;
            return this;
        }

        public ChatRoom.ChatRoomBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChatRoom build() {
            return new ChatRoom(this.id, this.users, this.messages, this.createdAt);
        }
    }
}
