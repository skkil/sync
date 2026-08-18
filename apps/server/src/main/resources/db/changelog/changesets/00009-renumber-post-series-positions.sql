--liquibase formatted sql
--changeset skkil:00009-renumber-post-series-positions

-- 게시글 삭제가 FK cascade 로만 편성 행을 지우던 동안 생긴 position 구멍을 1부터 연속으로 메운다.
-- 정렬 키는 조회 경로(findBySeriesIdOrderByPositionAscIdAsc)와 동일하게 (position, id) 를 쓴다.
WITH renumbered AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY post_series_id ORDER BY position, id) AS new_position
    FROM post_series_posts
)
UPDATE post_series_posts sp
SET position = r.new_position
FROM renumbered r
WHERE sp.id = r.id AND sp.position <> r.new_position;
