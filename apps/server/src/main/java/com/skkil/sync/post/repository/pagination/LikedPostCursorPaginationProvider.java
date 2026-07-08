package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.PostLikes.POST_LIKES;
import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.post.dto.data.LikedPostCursor;
import com.skkil.sync.post.dto.data.PostDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LikedPostCursorPaginationProvider
    extends KeysetCursorPaginationProvider<PostDto, LikedPostCursor> {

  @Override
  public Class<LikedPostCursor> getCursorClass() {
    return LikedPostCursor.class;
  }

  @Override
  protected List<KeysetField<LikedPostCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(POST_LIKES.CREATED_AT, LikedPostCursor::likedAt),
        KeysetField.desc(POSTS.ID, LikedPostCursor::postId));
  }

  @Override
  public LikedPostCursor convert(PostDto entity) {
    return new LikedPostCursor(entity.likedAt(), entity.id());
  }
}
