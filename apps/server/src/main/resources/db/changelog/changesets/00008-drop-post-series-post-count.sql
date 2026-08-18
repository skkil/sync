--liquibase formatted sql
--changeset skkil:00008-drop-post-series-post-count

-- post_series.post_count 는 추가 시에만 증가하고 게시글 삭제(FK cascade)로는 감소하지 않아
-- 실제 편 수와 어긋난다. 편 수는 post_series_posts 행 수에서 파생하도록 바꾸고 컬럼을 제거한다.
ALTER TABLE post_series DROP COLUMN post_count;
