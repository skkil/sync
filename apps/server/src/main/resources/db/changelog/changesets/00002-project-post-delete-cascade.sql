--liquibase formatted sql
--changeset skkil:00002-project-post-delete-cascade

ALTER TABLE comments
    DROP CONSTRAINT comments_post_id_fkey;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE;

ALTER TABLE posts
    DROP CONSTRAINT posts_project_id_fkey;

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_project
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;
