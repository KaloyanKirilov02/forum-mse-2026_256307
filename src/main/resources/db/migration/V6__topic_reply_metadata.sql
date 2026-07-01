ALTER TABLE posts
    ADD COLUMN author_id BIGINT REFERENCES users(id),
    ADD COLUMN modified_at TIMESTAMPTZ,
    ADD COLUMN views_count BIGINT NOT NULL DEFAULT 0;

ALTER TABLE replies
    ADD COLUMN author_id BIGINT REFERENCES users(id),
    ADD COLUMN modified_at TIMESTAMPTZ;

UPDATE posts
SET modified_at = created_at
WHERE modified_at IS NULL;

UPDATE replies
SET modified_at = created_at
WHERE modified_at IS NULL;

ALTER TABLE posts
    ALTER COLUMN modified_at SET NOT NULL;

ALTER TABLE replies
    ALTER COLUMN modified_at SET NOT NULL;

ALTER TABLE posts
    ADD CONSTRAINT uq_posts_title UNIQUE (title);