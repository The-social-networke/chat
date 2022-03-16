ALTER TABLE message
    DROP COLUMN updated_at;

ALTER TABLE message
    ADD COLUMN is_updated BOOLEAN DEFAULT FALSE;