CREATE TABLE read_message
(
    id              BIGSERIAL,
    message_id      VARCHAR(36)     NOT NULL,
    user_id         VARCHAR(36)     NOT NULL,

    CONSTRAINT pk_read_message
        PRIMARY KEY (id),
    CONSTRAINT fk_read_message
        FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,

    UNIQUE (message_id, user_id)
);

CREATE INDEX read_message_idx ON read_message (message_id, user_id);