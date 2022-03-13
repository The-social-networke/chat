CREATE TABLE read_message
(
    message_id      VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_read_message
        PRIMARY KEY (message_id, user_id),
    CONSTRAINT fk_read_message
        FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE
);
