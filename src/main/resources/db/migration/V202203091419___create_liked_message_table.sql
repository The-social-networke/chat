CREATE TABLE liked_message
(
    message_id      VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_like_message
        PRIMARY KEY (message_id, user_id),
    CONSTRAINT fk_like_message
        FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE
);

