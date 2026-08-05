--liquibase formatted sql
--changeset skkil:00001-project-list-indexes

CREATE INDEX idx_teammates_user_project
    ON teammates(user_id, project_id);

CREATE INDEX idx_posts_unresolved_project_questions
    ON posts(project_id)
    WHERE post_type = 'QUESTION'
      AND status = 'PUBLISHED'
      AND visibility = 'VISIBLE'
      AND resolved = false;
