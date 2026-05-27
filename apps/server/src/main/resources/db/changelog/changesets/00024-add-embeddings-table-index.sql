-- liquibase formatted sql
-- changeset hyoungjoojin:00024-add-embeddings-table-index
CREATE INDEX idx_reflection_embeddings_embedding_hnsw
    ON reflection_embeddings
    USING hnsw (embedding vector_cosine_ops);

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_reflections_content_gin
    ON reflections
    USING gin (content gin_trgm_ops);
