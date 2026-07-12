package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.post.dto.data.PostCursor;
import com.skkil.sync.post.dto.data.PostDto;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

/**
 * Orders and seeks on the derived "commented_posts" table that {@link
 * com.skkil.sync.post.repository.PostQueryRepository#getCommentedPosts} builds — a per-user
 * aggregate of {@code post_id} to the latest comment timestamp, needed because a user can comment
 * on the same post more than once.
 */
@Component
public class CommentedPostCursorPaginationProvider
    extends KeysetCursorPaginationProvider<PostDto, PostCursor> {

  public static final Field<OffsetDateTime> COMMENTED_AT =
      DSL.field(DSL.name("commented_posts", "commentedAt"), OffsetDateTime.class);

  @Override
  public Class<PostCursor> getCursorClass() {
    return PostCursor.class;
  }

  @Override
  protected List<KeysetField<PostCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(COMMENTED_AT, PostCursor::sortKey),
        KeysetField.desc(POSTS.ID, PostCursor::postId));
  }

  @Override
  public PostCursor convert(PostDto entity) {
    return new PostCursor(entity.sortKey(), entity.id());
  }
}
