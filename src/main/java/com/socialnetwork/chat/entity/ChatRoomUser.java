package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "user__chat_room")
public class ChatRoomUser {

    @EmbeddedId
    @JsonUnwrapped
    private ChatRoomUserPk id;

    @JsonIgnore
    @MapsId("chatRoomId")
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
}
