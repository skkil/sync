package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationCursor;
import java.util.List;

/**
 * 채널마다 정렬 기준 컬럼(생성 시각, 좋아요 수 등)만 다를 뿐 정렬 후 seek 조건을 만드는 방식은 동일하므로, 정렬 기준 필드를 생성자로 주입받아 채널 간에 공유한다.
 */
public class PostRecommendationPaginationProvider
    extends KeysetCursorPaginationProvider<PostRecommendationCandidate, PostRecommendationCursor> {

  public static final PostRecommendationPaginationProvider CREATED_AT =
      new PostRecommendationPaginationProvider(
          KeysetField.desc(POSTS.CREATED_AT, PostRecommendationCursor::createdAt));

  public static final PostRecommendationPaginationProvider LIKE_COUNT =
      new PostRecommendationPaginationProvider(
          KeysetField.desc(POSTS.LIKE_COUNT, PostRecommendationCursor::likeCount));

  private final List<KeysetField<PostRecommendationCursor, ?>> keysetFields;

  private PostRecommendationPaginationProvider(KeysetField<PostRecommendationCursor, ?> sortField) {
    this.keysetFields =
        List.of(sortField, KeysetField.desc(POSTS.ID, PostRecommendationCursor::id));
  }

  @Override
  public Class<PostRecommendationCursor> getCursorClass() {
    return PostRecommendationCursor.class;
  }

  @Override
  protected List<KeysetField<PostRecommendationCursor, ?>> getKeysetFields() {
    return keysetFields;
  }

  @Override
  public PostRecommendationCursor convert(PostRecommendationCandidate entity) {
    return new PostRecommendationCursor(entity.createdAt(), entity.likeCount(), entity.id());
  }
}
