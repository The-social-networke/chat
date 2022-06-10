package com.socialnetwork.chat.entity;

import com.socialnetwork.chat.util.enums.ForwardType;
import com.socialnetwork.chat.util.enums.MessageStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "message")
@Builder(toBuilder = true)
@EqualsAndHashCode
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
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_updated", columnDefinition = "boolean default false")
    private boolean isUpdated = false;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<MessageReaders> messageReads = new HashSet<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<MessageLikes> messageLikes = new HashSet<>();

    @Transient
    private MessageStatus messageStatus;
}
