package com.socialnetwork.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "read_message")
public class MessageReaders {

    @EmbeddedId
    @JsonUnwrapped
    private MessageUserPk id;

    @JsonIgnore
    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Message message;


}
