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
@Table(name = "user__chat_room")
public class ChatRoomUser {

    @EmbeddedId
    @JsonUnwrapped
    private ChatRoomUserPk chatRoomUserPk;

    @JsonIgnore
    @MapsId("chatRoomId")
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
}
