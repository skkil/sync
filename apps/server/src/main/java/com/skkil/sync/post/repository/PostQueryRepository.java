package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.Comments.COMMENTS;
import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.PostLikes.POST_LIKES;
import static com.skkil.sync.jooq.tables.PostTags.POST_TAGS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Teammates.TEAMMATES;
import static com.skkil.sync.jooq.tables.Users.USERS;
import static com.skkil.sync.post.repository.pagination.CommentedPostCursorPaginationProvider.COMMENTED_AT;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.model.PostScope;
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
    return dsl.select(post(requesterId, true))
        .from(POSTS)
        .leftJoin(PROJECTS)
        .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
        .where(POSTS.SLUG.eq(slug).and(Conditions.readableCondition(requesterId)))
        .fetchOptionalInto(PostDto.class);
  }

  public CursorPaginationDataFetcher<PostDto> getPosts(Long requesterId) {
    return (condition, orderFields, size) ->
        dsl.select(post(requesterId))
            .from(POSTS)
            .leftJoin(PROJECTS)
            .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
            .where(condition.and(Conditions.feedVisibleCondition()))
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

  public CursorPaginationDataFetcher<PostDto> getPostsByTag(
      Long requesterId, Long tagId, PostType type) {
    return (condition, orderFields, size) -> {
      Condition tagCondition = condition.and(POST_TAGS.TAG_ID.eq(tagId));
      if (type != null) {
        tagCondition = tagCondition.and(POSTS.POST_TYPE.eq(type.name()));
      }

      return dsl.select(post(requesterId))
          .from(POSTS)
          .join(POST_TAGS)
          .on(POST_TAGS.POST_ID.eq(POSTS.ID))
          .leftJoin(PROJECTS)
          .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
          .where(tagCondition.and(Conditions.tagPostVisibilityCondition(requesterId)))
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(PostDto.class);
    };
  }

  public CursorPaginationDataFetcher<PostDto> getPostsByProject(
      Long requesterId, String handle, PostType type, String authorHandle) {
    return (condition, orderFields, size) -> {
      Condition projectCondition =
          condition
              .and(PROJECTS.HANDLE.eq(handle))
              .and(Conditions.workspacePublishedCondition())
              .and(
                  Conditions.publicProjectCondition()
                      .or(Conditions.workspaceReadableCondition(requesterId)));
      if (type != null) {
        projectCondition = projectCondition.and(POSTS.POST_TYPE.eq(type.name()));
      }
      if (authorHandle != null) {
        projectCondition = projectCondition.and(USERS.HANDLE.eq(authorHandle));
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

  public CursorPaginationDataFetcher<PostDto> getDraftsByAuthor(
      Long requesterId, PostType type, PostScope scope, String projectHandle) {
    return (condition, orderFields, size) -> {
      Condition draftCondition =
          condition
              .and(POSTS.AUTHOR_ID.eq(requesterId))
              .and(POSTS.STATUS.eq(PostStatus.DRAFT.name()))
              .and(Conditions.visibleCondition());

      if (type != null) {
        draftCondition = draftCondition.and(POSTS.POST_TYPE.eq(type.name()));
      }

      if (scope != null) {
        draftCondition = draftCondition.and(POSTS.SCOPE.eq(scope.name()));
      }

      if (projectHandle != null) {
        draftCondition = draftCondition.and(PROJECTS.HANDLE.eq(projectHandle));
      }

      return dsl.select(post(requesterId))
          .from(POSTS)
          .leftJoin(PROJECTS)
          .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
          .where(draftCondition)
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(PostDto.class);
    };
  }

  public CursorPaginationDataFetcher<PostDto> getBookmarkedPosts(
      Long userId, String projectHandle) {
    return (condition, orderFields, size) -> {
      Condition bookmarkCondition =
          condition.and(POST_BOOKMARKS.USER_ID.eq(userId)).and(Conditions.visibleCondition());

      if (projectHandle != null) {
        bookmarkCondition = bookmarkCondition.and(PROJECTS.HANDLE.eq(projectHandle));
      }

      return dsl.select(post(userId, POST_BOOKMARKS.CREATED_AT))
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
          condition.and(POST_LIKES.USER_ID.eq(userId)).and(Conditions.visibleCondition());

      if (projectHandle != null) {
        likeCondition = likeCondition.and(PROJECTS.HANDLE.eq(projectHandle));
      }

      return dsl.select(post(userId, POST_LIKES.CREATED_AT))
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

  public CursorPaginationDataFetcher<PostDto> getCommentedPosts(Long userId, String projectHandle) {
    return (condition, orderFields, size) -> {
      var commentedPosts =
          dsl.select(COMMENTS.POST_ID.as("postId"), DSL.max(COMMENTS.CREATED_AT).as("commentedAt"))
              .from(COMMENTS)
              .where(COMMENTS.AUTHOR_ID.eq(userId).and(COMMENTS.DELETED_AT.isNull()))
              .groupBy(COMMENTS.POST_ID)
              .asTable(DSL.name("commented_posts"));

      Condition commentedCondition = condition.and(Conditions.visibleCondition());
      if (projectHandle != null) {
        commentedCondition = commentedCondition.and(PROJECTS.HANDLE.eq(projectHandle));
      }

      return dsl.select(post(userId, COMMENTED_AT))
          .from(commentedPosts)
          .join(POSTS)
          .on(POSTS.ID.eq(DSL.field(DSL.name("commented_posts", "postId"), Long.class)))
          .leftJoin(PROJECTS)
          .on(POSTS.PROJECT_ID.eq(PROJECTS.ID))
          .where(commentedCondition)
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(PostDto.class);
    };
  }

  public List<PostDto> getPostsByIds(Long requesterId, List<Long> ids) {
    return getPostsByIds(
        requesterId, ids, POSTS.ID.in(ids).and(Conditions.publicPublishedCondition()));
  }

  public List<PostDto> getPostsByIdsInProject(
      Long requesterId, List<Long> ids, String projectHandle) {
    return getPostsByIds(
        requesterId,
        ids,
        POSTS
            .ID
            .in(ids)
            .and(Conditions.workspacePublishedCondition())
            .and(
                Conditions.publicProjectCondition()
                    .or(Conditions.workspaceReadableCondition(requesterId)))
            .and(PROJECTS.HANDLE.eq(projectHandle)));
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
    return post(requesterId, POSTS.CREATED_AT, false);
  }

  private List<SelectFieldOrAsterisk> post(Long requesterId, boolean shouldFetchContent) {
    return post(requesterId, POSTS.CREATED_AT, shouldFetchContent);
  }

  private List<SelectFieldOrAsterisk> post(Long requesterId, Field<OffsetDateTime> sortKey) {
    return post(requesterId, sortKey, false);
  }

  // Shared by every query method below. POSTS.CONTENT is only ever the real column for
  // getPostBySlug (shouldFetchContent = true) — every list-shaped query gets a null
  // placeholder in its place instead of paying for the unbounded payload, while still
  // keeping the selected column count aligned with PostDto's record components.
  private List<SelectFieldOrAsterisk> post(
      Long requesterId, Field<OffsetDateTime> sortKey, boolean shouldFetchContent) {
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

    Field<String> content =
        shouldFetchContent ? POSTS.CONTENT : DSL.value((String) null, POSTS.CONTENT.getDataType());

    return List.of(
        POSTS.ID.as("id"),
        POSTS.POST_TYPE.as("type"),
        POSTS.STATUS.as("status"),
        POSTS.SCOPE.as("scope"),
        POSTS.SLUG.as("slug"),
        POSTS.TITLE.as("title"),
        POSTS.AUTHOR_ID.as("authorId"),
        PROJECTS.HANDLE.as("projectHandle"),
        PROJECTS.NAME.as("projectName"),
        PROJECTS.DESCRIPTION.as("projectDescription"),
        PROJECTS.WEBSITE_URL.as("projectWebsite"),
        PROJECTS.IS_PUBLIC.as("projectIsPublic"),
        content.as("content"),
        POSTS.CREATED_AT.as("createdAt"),
        POSTS.UPDATED_AT.as("updatedAt"),
        POSTS.LIKE_COUNT.as("likeCount"),
        DSL.value(0L).as("commentCount"),
        liked.as("liked"),
        bookmarked.as("bookmarked"),
        POSTS.RESOLVED.as("resolved"),
        POSTS.PREVIEW.as("preview"),
        POSTS.MEDIA_COUNT.as("mediaCount"),
        POSTS.WORD_COUNT.as("wordCount"),
        sortKey.as("sortKey"));
  }

  private static final class Conditions {
    private static Condition visibleCondition() {
      return POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name());
    }

    private static Condition publishedCondition() {
      return POSTS.STATUS.eq(PostStatus.PUBLISHED.name());
    }

    private static Condition publicPublishedCondition() {
      return visibleCondition()
          .and(POSTS.SCOPE.eq(PostScope.PUBLIC.name()))
          .and(publishedCondition());
    }

    private static Condition workspacePublishedCondition() {
      return visibleCondition().and(POSTS.PROJECT_ID.isNotNull()).and(publishedCondition());
    }

    // 프로젝트가 공개(public)로 설정된 경우, 해당 프로젝트의 게시글은 팀원이 아니어도 읽을 수 있다.
    private static Condition publicProjectCondition() {
      return PROJECTS.IS_PUBLIC.isTrue();
    }

    // 공개 피드(전체 게시글 목록)에 노출 가능한 게시글: 프로젝트에 속하지 않은 개인 게시글이거나,
    // 공개 프로젝트에 속한 게시글. 비공개 프로젝트 게시글은 작성자/팀원 여부와 무관하게 피드에서 제외한다.
    private static Condition feedVisibleCondition() {
      return publicPublishedCondition()
          .or(workspacePublishedCondition().and(publicProjectCondition()));
    }

    private static Condition tagPostVisibilityCondition(Long requesterId) {
      return visibleCondition()
          .and(publishedCondition())
          .and(
              POSTS
                  .SCOPE
                  .eq(PostScope.PUBLIC.name())
                  .or(publicProjectCondition())
                  .or(workspaceReadableCondition(requesterId)));
    }

    private static Condition workspaceReadableCondition(Long requesterId) {
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

    private static Condition readableCondition(Long requesterId) {
      Condition publiclyReadable =
          publicPublishedCondition()
              .or(workspacePublishedCondition().and(publicProjectCondition()));
      if (requesterId == null) {
        return publiclyReadable;
      }

      return visibleCondition()
          .and(
              publiclyReadable
                  .or(POSTS.AUTHOR_ID.eq(requesterId))
                  .or(workspacePublishedCondition().and(workspaceReadableCondition(requesterId))));
    }
  }
}
