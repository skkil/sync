package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.PostLikes.POST_LIKES;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Teammates.TEAMMATES;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.PostVisibility;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryRepository {

  private final DSLContext dsl;

  public PostQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Optional<PostDto> getPostBySlug(Long requesterId, String slug) {
    return dsl.select(post(requesterId))
        .from(POSTS)
        .leftJoin(PROJECTS)
        .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
        .where(POSTS.SLUG.eq(slug).and(readableCondition(requesterId)))
        .fetchOptional()
        .map(record -> record.into(PostDto.class));
  }

  public CursorPaginationDataFetcher<PostDto> getPosts(Long requesterId) {
    return (condition, orderFields, size) ->
        dsl.select(post(requesterId))
            .from(POSTS)
            .leftJoin(PROJECTS)
            .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
            .where(condition.and(publicPublishedCondition()))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(PostDto.class);
  }

  public CursorPaginationDataFetcher<PostDto> getPostsByUser(
      Long requesterId, Long userId, PostType type) {
    return (condition, orderFields, size) -> {
      Condition userCondition = condition.and(POSTS.AUTHOR_ID.eq(userId));
      if (type != null) {
        userCondition = userCondition.and(POSTS.POST_TYPE.eq(type.name()));
      }

      CursorPaginationDataFetcher<PostDto> base = getPosts(requesterId);
      return base.fetch(userCondition, orderFields, size);
    };
  }

  public CursorPaginationDataFetcher<PostDto> getPostsByProject(
      Long requesterId, String handle, PostType type) {
    return (condition, orderFields, size) -> {
      Condition projectCondition =
          condition
              .and(PROJECTS.HANDLE.eq(handle))
              .and(workspacePublishedCondition())
              .and(workspaceReadableCondition(requesterId));
      if (type != null) {
        projectCondition = projectCondition.and(POSTS.POST_TYPE.eq(type.name()));
      }

      return dsl.select(post(requesterId))
          .from(POSTS)
          .join(USERS)
          .on(POSTS.AUTHOR_ID.eq(USERS.ID))
          .join(PROJECTS)
          .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
          .where(projectCondition)
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(PostDto.class);
    };
  }

  public CursorPaginationDataFetcher<PostDto> getBookmarkedPosts(
      Long userId, String projectHandle) {
    return (condition, orderFields, size) -> {
      Condition bookmarkCondition =
          condition.and(POST_BOOKMARKS.USER_ID.eq(userId)).and(visibleCondition());

      if (projectHandle != null) {
        bookmarkCondition = bookmarkCondition.and(PROJECTS.HANDLE.eq(projectHandle));
      }

      return dsl.select(post(userId, POST_BOOKMARKS.CREATED_AT, DSL.castNull(OffsetDateTime.class)))
          .from(POST_BOOKMARKS)
          .join(POSTS)
          .on(POST_BOOKMARKS.POST_ID.eq(POSTS.ID))
          .leftJoin(PROJECTS)
          .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
          .where(bookmarkCondition)
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(PostDto.class);
    };
  }

  public CursorPaginationDataFetcher<PostDto> getLikedPosts(Long userId, String projectHandle) {
    return (condition, orderFields, size) -> {
      Condition likeCondition =
          condition.and(POST_LIKES.USER_ID.eq(userId)).and(visibleCondition());

      if (projectHandle != null) {
        likeCondition = likeCondition.and(PROJECTS.HANDLE.eq(projectHandle));
      }

      return dsl.select(post(userId, DSL.castNull(OffsetDateTime.class), POST_LIKES.CREATED_AT))
          .from(POST_LIKES)
          .join(POSTS)
          .on(POST_LIKES.POST_ID.eq(POSTS.ID))
          .leftJoin(PROJECTS)
          .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
          .where(likeCondition)
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(PostDto.class);
    };
  }

  public List<PostDto> getPostsByIds(Long requesterId, List<Long> ids) {
    return getPostsByIds(requesterId, ids, POSTS.ID.in(ids).and(publicPublishedCondition()));
  }

  public List<PostDto> getPostsByIdsInProject(
      Long requesterId, List<Long> ids, String projectHandle) {
    return getPostsByIds(
        requesterId,
        ids,
        POSTS.ID.in(ids).and(publicPublishedCondition()).and(PROJECTS.HANDLE.eq(projectHandle)));
  }

  private List<PostDto> getPostsByIds(Long requesterId, List<Long> ids, Condition condition) {
    if (ids.isEmpty()) {
      return List.of();
    }

    Map<Long, PostDto> byId =
        dsl
            .select(post(requesterId))
            .from(POSTS)
            .leftJoin(PROJECTS)
            .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
            .where(condition)
            .fetchInto(PostDto.class)
            .stream()
            .collect(Collectors.toMap(PostDto::id, Function.identity()));

    return ids.stream().map(byId::get).filter(dto -> dto != null).toList();
  }

  private List<SelectFieldOrAsterisk> post(Long requesterId) {
    return post(
        requesterId, DSL.castNull(OffsetDateTime.class), DSL.castNull(OffsetDateTime.class));
  }

  private List<SelectFieldOrAsterisk> post(
      Long requesterId, Field<OffsetDateTime> bookmarkedAt, Field<OffsetDateTime> likedAt) {
    Field<Boolean> bookmarked =
        requesterId == null
            ? DSL.value(false)
            : DSL.field(
                DSL.exists(
                    DSL.selectOne()
                        .from(POST_BOOKMARKS)
                        .where(POST_BOOKMARKS.POST_ID.eq(POSTS.ID))
                        .and(POST_BOOKMARKS.USER_ID.eq(requesterId))));

    Field<Boolean> liked =
        requesterId == null
            ? DSL.value(false)
            : DSL.field(
                DSL.exists(
                    DSL.selectOne()
                        .from(POST_LIKES)
                        .where(POST_LIKES.POST_ID.eq(POSTS.ID))
                        .and(POST_LIKES.USER_ID.eq(requesterId))));

    return List.of(
        POSTS.ID.as("id"),
        POSTS.POST_TYPE.as("type"),
        POSTS.STATUS.as("status"),
        POSTS.SLUG.as("slug"),
        POSTS.TITLE.as("title"),
        POSTS.AUTHOR_ID.as("authorId"),
        PROJECTS.HANDLE.as("projectHandle"),
        PROJECTS.NAME.as("projectName"),
        PROJECTS.DESCRIPTION.as("projectDescription"),
        PROJECTS.WEBSITE_URL.as("projectWebsite"),
        PROJECTS.IS_PUBLIC.as("projectIsPublic"),
        POSTS.CONTENT.as("content"),
        POSTS.CREATED_AT.as("createdAt"),
        POSTS.UPDATED_AT.as("updatedAt"),
        POSTS.LIKE_COUNT.as("likeCount"),
        DSL.value(0L).as("commentCount"),
        liked.as("liked"),
        bookmarked.as("bookmarked"),
        POSTS.RESOLVED.as("resolved"),
        bookmarkedAt.as("bookmarkedAt"),
        likedAt.as("likedAt"));
  }

  private Condition visibleCondition() {
    return POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name());
  }

  private Condition publicPublishedCondition() {
    return visibleCondition()
        .and(POSTS.PROJECT_ID.isNull())
        .and(POSTS.STATUS.eq(PostStatus.PUBLISHED.name()));
  }

  private Condition workspacePublishedCondition() {
    return visibleCondition()
        .and(POSTS.PROJECT_ID.isNotNull())
        .and(POSTS.STATUS.eq(PostStatus.PUBLISHED.name()));
  }

  private Condition workspaceReadableCondition(Long requesterId) {
    if (requesterId == null) {
      return DSL.falseCondition();
    }

    return POSTS
        .AUTHOR_ID
        .eq(requesterId)
        .or(
            DSL.exists(
                DSL.selectOne()
                    .from(TEAMMATES)
                    .where(TEAMMATES.PROJECT_ID.eq(POSTS.PROJECT_ID))
                    .and(TEAMMATES.USER_ID.eq(requesterId))));
  }

  private Condition readableCondition(Long requesterId) {
    Condition publicPost = publicPublishedCondition();
    if (requesterId == null) {
      return publicPost;
    }

    return visibleCondition()
        .and(
            publicPost
                .or(POSTS.AUTHOR_ID.eq(requesterId))
                .or(workspacePublishedCondition().and(workspaceReadableCondition(requesterId))));
  }
}
