--liquibase formatted sql
--changeset skkil:00003-project-delete-indexes

CREATE INDEX idx_post_tags_tag_id
    ON post_tags(tag_id);

CREATE INDEX idx_posts_project_id
    ON posts(project_id)
    WHERE project_id IS NOT NULL;

CREATE INDEX idx_project_invitations_project_id
    ON project_invitations(project_id);
