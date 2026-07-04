--liquibase formatted sql
--changeset skkil:00003-comment-pagination-index

CREATE INDEX idx_comments_post_created_id ON comments(post_id, created_at DESC, id DESC);
