ALTER TABLE message
    DROP CONSTRAINT fk_message,
    ADD CONSTRAINT fk_message
        FOREIGN KEY (chat_room_id)  REFERENCES chat_room ON DELETE CASCADE