package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatRoomUserPk implements Serializable {

    private static final long serialVersionUID = 6563780332589068936L;

    @Column(name = "user_id")
    private String userId;

    @JsonIgnore
    @Column(name = "chat_room_id")
    private String chatRoomId;
}
