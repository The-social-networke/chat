CREATE TABLE liked_message
(
    id              BIGSERIAL,
    message_id      VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_like_message
        PRIMARY KEY (id),
    CONSTRAINT fk_like_message
        FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,

    UNIQUE (message_id, user_id)
);

CREATE INDEX liked_message_idx ON liked_message (message_id, user_id);

