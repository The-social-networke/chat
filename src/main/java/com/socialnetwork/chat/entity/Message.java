package com.socialnetwork.chat.entity;

import com.socialnetwork.chat.model.enums.ForwardType;
import com.socialnetwork.chat.model.enums.MessageStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @Column(updatable = false)
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

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_updated")
    private boolean isUpdated;

    @Column(name = "is_system")
    private boolean isSystem;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<MessageReaders> messageReads = new HashSet<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<MessageLike> messageLikes = new HashSet<>();

    @Transient
    private MessageStatus messageStatus;


    public Message() {
    }

    public Message(String id, String userId, String text, Byte[] photo, String forwardId, ForwardType forwardType, LocalDateTime sentAt, boolean isUpdated, boolean isSystem, ChatRoom chatRoom, Set<MessageReaders> messageReads, Set<MessageLike> messageLikes, MessageStatus messageStatus) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.photo = photo;
        this.forwardId = forwardId;
        this.forwardType = forwardType;
        this.sentAt = sentAt;
        this.isUpdated = isUpdated;
        this.isSystem = isSystem;
        this.chatRoom = chatRoom;
        this.messageReads = messageReads;
        this.messageLikes = messageLikes;
        this.messageStatus = messageStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(Byte[] photo) {
        this.photo = photo;
    }

    public String getForwardId() {
        return forwardId;
    }

    public void setForwardId(String forwardId) {
        this.forwardId = forwardId;
    }

    public ForwardType getForwardType() {
        return forwardType;
    }

    public void setForwardType(ForwardType forwardType) {
        this.forwardType = forwardType;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public Set<MessageReaders> getMessageReads() {
        return messageReads;
    }

    public void setMessageReads(Set<MessageReaders> messageReads) {
        this.messageReads = messageReads;
    }

    public Set<MessageLike> getMessageLikes() {
        return messageLikes;
    }

    public void setMessageLikes(Set<MessageLike> messageLikes) {
        this.messageLikes = messageLikes;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return isUpdated == message.isUpdated && isSystem == message.isSystem && Objects.equals(id, message.id) && Objects.equals(userId, message.userId) && Objects.equals(text, message.text) && Arrays.equals(photo, message.photo) && Objects.equals(forwardId, message.forwardId) && forwardType == message.forwardType && Objects.equals(sentAt, message.sentAt) && Objects.equals(chatRoom, message.chatRoom) && Objects.equals(messageReads, message.messageReads) && Objects.equals(messageLikes, message.messageLikes) && messageStatus == message.messageStatus;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, userId, text, forwardId, forwardType, sentAt, isUpdated, isSystem, chatRoom, messageReads, messageLikes, messageStatus);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }

    public static Message.MessageBuilder builder() {
        return new Message.MessageBuilder();
    }

    public Message.MessageBuilder toBuilder() {
        return (new Message.MessageBuilder()).id(this.id).userId(this.userId).text(this.text).photo(this.photo).forwardId(this.forwardId).forwardType(this.forwardType).sentAt(this.sentAt).isUpdated(this.isUpdated).isSystem(this.isSystem).chatRoom(this.chatRoom).messageReads(this.messageReads).messageLikes(this.messageLikes).messageStatus(this.messageStatus);
    }

    public static class MessageBuilder {
        private String id;
        private String userId;
        private String text;
        private Byte[] photo;
        private String forwardId;
        private ForwardType forwardType;
        private LocalDateTime sentAt;
        private boolean isUpdated;
        private boolean isSystem;
        private ChatRoom chatRoom;
        private Set<MessageReaders> messageReads;
        private Set<MessageLike> messageLikes;
        private MessageStatus messageStatus;

        MessageBuilder() {
        }

        public Message.MessageBuilder id(String id) {
            this.id = id;
            return this;
        }

        public Message.MessageBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Message.MessageBuilder text(String text) {
            this.text = text;
            return this;
        }

        public Message.MessageBuilder photo(Byte[] photo) {
            this.photo = photo;
            return this;
        }

        public Message.MessageBuilder forwardId(String forwardId) {
            this.forwardId = forwardId;
            return this;
        }

        public Message.MessageBuilder forwardType(ForwardType forwardType) {
            this.forwardType = forwardType;
            return this;
        }

        public Message.MessageBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Message.MessageBuilder isUpdated(boolean isUpdated) {
            this.isUpdated = isUpdated;
            return this;
        }

        public Message.MessageBuilder isSystem(boolean isSystem) {
            this.isSystem = isSystem;
            return this;
        }

        public Message.MessageBuilder chatRoom(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
            return this;
        }

        public Message.MessageBuilder messageReads(Set<MessageReaders> messageReads) {
            this.messageReads = messageReads;
            return this;
        }

        public Message.MessageBuilder messageLikes(Set<MessageLike> messageLikes) {
            this.messageLikes = messageLikes;
            return this;
        }

        public Message.MessageBuilder messageStatus(MessageStatus messageStatus) {
            this.messageStatus = messageStatus;
            return this;
        }

        public Message build() {
            return new Message(this.id, this.userId, this.text, this.photo, this.forwardId, this.forwardType, this.sentAt, this.isUpdated, this.isSystem, this.chatRoom, this.messageReads, this.messageLikes, this.messageStatus);
        }
    }
}
