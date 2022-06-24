CREATE TABLE user__chat_room
(
    id              BIGSERIAL,
    chat_room_id    VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_user__chat_room
        PRIMARY KEY (id),
    CONSTRAINT fk_user__chat_room
        FOREIGN KEY (chat_room_id) REFERENCES chat_room ON DELETE CASCADE,

    UNIQUE (chat_room_id, user_id)
);

CREATE INDEX user__chat_room_idx ON user__chat_room (chat_room_id, user_id);

