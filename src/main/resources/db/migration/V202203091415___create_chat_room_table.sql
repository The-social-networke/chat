CREATE TABLE chat_room
(
    id          VARCHAR(36)     NOT NULL,
    created_at  TIMESTAMP       NOT NULL,

    CONSTRAINT pk_chat
        PRIMARY KEY (id)
);
