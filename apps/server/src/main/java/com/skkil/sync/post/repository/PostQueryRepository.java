package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.post.dto.data.PostDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryRepository {

  private final DSLContext dsl;

  public PostQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Optional<PostDto> getPostBySlug(Long requesterId, String slug) {
    return dsl.select(
            POSTS.ID.as("id"),
            POSTS.SLUG.as("slug"),
            POSTS.AUTHOR_ID.as("authorId"),
            USERS.FULL_NAME.as("authorName"),
            PROJECTS.ID.as("projectId"),
            PROJECTS.NAME.as("projectName"),
            POSTS.CONTENT.as("content"),
            POSTS.CREATED_AT.as("createdAt"),
            POSTS.UPDATED_AT.as("updatedAt"),
            POSTS.LIKE_COUNT.as("likeCount"),
            DSL.value(0L).as("commentCount"),
            bookmarkedField(requesterId))
        .from(POSTS)
        .join(USERS)
        .on(POSTS.AUTHOR_ID.eq(USERS.ID))
        .leftJoin(PROJECTS)
        .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
        .where(POSTS.SLUG.eq(slug))
        .fetchOptional()
        .map(record -> record.into(PostDto.class));
  }

  public CursorPaginationDataFetcher<PostDto> getPosts() {
    return (condition, orderFields, size) ->
        dsl.select(
                POSTS.ID.as("id"),
                POSTS.SLUG.as("slug"),
                POSTS.AUTHOR_ID.as("authorId"),
                USERS.FULL_NAME.as("authorName"),
                PROJECTS.ID.as("projectId"),
                PROJECTS.NAME.as("projectName"),
                POSTS.CONTENT.as("content"),
                POSTS.CREATED_AT.as("createdAt"),
                POSTS.UPDATED_AT.as("updatedAt"),
                POSTS.LIKE_COUNT.as("likeCount"),
                DSL.value(0L).as("commentCount"),
                bookmarkedField(null))
            .from(POSTS)
            .join(USERS)
            .on(POSTS.AUTHOR_ID.eq(USERS.ID))
            .leftJoin(PROJECTS)
            .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
            .where(condition)
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(PostDto.class);
  }

  public CursorPaginationDataFetcher<PostDto> getPostsByUser(Long userId) {
    return (condition, orderFields, size) -> {
      CursorPaginationDataFetcher<PostDto> base = getPosts();
      return base.fetch(condition.and(POSTS.AUTHOR_ID.eq(userId)), orderFields, size);
    };
  }

  public CursorPaginationDataFetcher<PostDto> getPostsByProject(String handle) {
    return (condition, orderFields, size) -> {
      CursorPaginationDataFetcher<PostDto> base = getPosts();
      return base.fetch(condition.and(PROJECTS.HANDLE.eq(handle)), orderFields, size);
    };
  }

  public List<PostDto> getPostsByIds(List<Long> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }

    Map<Long, PostDto> byId =
        dsl
            .select(
                POSTS.ID.as("id"),
                POSTS.SLUG.as("slug"),
                POSTS.AUTHOR_ID.as("authorId"),
                USERS.FULL_NAME.as("authorName"),
                PROJECTS.ID.as("projectId"),
                PROJECTS.NAME.as("projectName"),
                POSTS.CONTENT.as("content"),
                POSTS.CREATED_AT.as("createdAt"),
                POSTS.UPDATED_AT.as("updatedAt"),
                POSTS.LIKE_COUNT.as("likeCount"),
                DSL.value(0L).as("commentCount"),
                bookmarkedField(null))
            .from(POSTS)
            .join(USERS)
            .on(POSTS.AUTHOR_ID.eq(USERS.ID))
            .leftJoin(PROJECTS)
            .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
            .where(POSTS.ID.in(ids))
            .fetchInto(PostDto.class)
            .stream()
            .collect(Collectors.toMap(PostDto::id, Function.identity()));

    return ids.stream().map(byId::get).filter(dto -> dto != null).toList();
  }

  private Field<Boolean> bookmarkedField(Long requesterId) {
    if (requesterId == null) {
      return DSL.value(false).as("bookmarked");
    }

    return DSL.field(
            DSL.exists(
                DSL.selectOne()
                    .from(POST_BOOKMARKS)
                    .where(POST_BOOKMARKS.POST_ID.eq(POSTS.ID))
                    .and(POST_BOOKMARKS.USER_ID.eq(requesterId))))
        .as("bookmarked");
  }
}
