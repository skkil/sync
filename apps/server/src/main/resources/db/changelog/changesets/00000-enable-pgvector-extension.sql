--liquibase formatted sql
--changeset hyoungjoojin:00000-enable-pgvector-extension
CREATE EXTENSION IF NOT EXISTS vector;
