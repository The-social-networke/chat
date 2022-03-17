ALTER TABLE message
    DROP COLUMN content_id;

ALTER TABLE message
    ADD COLUMN forward_id   VARCHAR(36)     DEFAULT NULL,
    ADD COLUMN forward_type varchar(12)     DEFAULT NULL;