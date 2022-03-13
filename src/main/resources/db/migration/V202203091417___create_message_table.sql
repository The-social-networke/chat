CREATE TABLE message
(
    id              VARCHAR(36)     NOT NULL,
    chat_room_id    VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,
    text            TEXT,
    photo           BYTEA,
    sent_at         TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,

    CONSTRAINT pk_message
        PRIMARY KEY (id),
    CONSTRAINT fk_message
        FOREIGN KEY (chat_room_id) REFERENCES chat_room(id)
);
