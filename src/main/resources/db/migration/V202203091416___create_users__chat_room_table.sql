CREATE TABLE user__chat_room
(
    chat_room_id    VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_user__chat_room
        PRIMARY KEY (chat_room_id, user_id),
    CONSTRAINT fk_user__chat_room
        FOREIGN KEY (chat_room_id) REFERENCES chat_room ON DELETE CASCADE
);

