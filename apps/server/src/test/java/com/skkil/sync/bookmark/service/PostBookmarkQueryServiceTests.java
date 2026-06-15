package com.skkil.sync.bookmark.service;

import static com.skkil.sync.jooq.tables.MediaFiles.MEDIA_FILES;
import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Users.USERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.bookmark.mapper.PostBookmarkMapper;
import com.skkil.sync.bookmark.repository.PostBookmarkQueryRepository;
import com.skkil.sync.bookmark.repository.pagination.BookmarkedPostCursorPaginationProvider;
import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.media.service.domain.MediaDomainService;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
  TestcontainersConfig.class,
  PostBookmarkQueryRepository.class,
  PostBookmarkQueryService.class,
  BookmarkedPostCursorPaginationProvider.class,
  PaginationService.class,
  CursorConverter.class,
  PostBookmarkQueryServiceTests.TestConfig.class
})
class PostBookmarkQueryServiceTests {

  private static final Long REQUESTER_ID = 1L;
  private static final Long AUTHOR_WITH_IMAGE_ID = 2L;
  private static final Long AUTHOR_WITHOUT_IMAGE_ID = 3L;
  private static final Long OTHER_USER_ID = 4L;
  private static final Long PROFILE_IMAGE_ID = 100L;
  private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 5, 1, 12, 0);
  private static final LocalDateTime NEWEST_BOOKMARKED_AT = LocalDateTime.of(2026, 5, 5, 12, 0);
  private static final LocalDateTime TIED_BOOKMARKED_AT = LocalDateTime.of(2026, 5, 4, 12, 0);
  private static final LocalDateTime OLDEST_BOOKMARKED_AT = LocalDateTime.of(2026, 5, 3, 12, 0);

  @Autowired private DSLContext dsl;
  @Autowired private PaginationService paginationService;
  @Autowired private PostBookmarkQueryRepository postBookmarkQueryRepository;
  @Autowired private PostBookmarkQueryService postBookmarkQueryService;
  @Autowired private BookmarkedPostCursorPaginationProvider paginationProvider;
  @Autowired private MediaDomainService mediaDomainService;

  @BeforeEach
  void setUp() {
    dsl.execute(
        "TRUNCATE TABLE post_bookmarks, posts, media_files, users RESTART IDENTITY CASCADE");

    insertUser(REQUESTER_ID, "requester@email.com", "requester", "Requester", null);
    insertUser(
        AUTHOR_WITH_IMAGE_ID, "image-author@email.com", "image_author", "Image Author", null);
    insertUser(
        AUTHOR_WITHOUT_IMAGE_ID, "plain-author@email.com", "plain_author", "Plain Author", null);
    insertUser(OTHER_USER_ID, "other@email.com", "other", "Other User", null);
    insertMedia(PROFILE_IMAGE_ID, AUTHOR_WITH_IMAGE_ID);
    updateProfileImage(AUTHOR_WITH_IMAGE_ID, PROFILE_IMAGE_ID);

    insertPost(101L, "newest", AUTHOR_WITH_IMAGE_ID);
    insertPost(102L, "tie-high", AUTHOR_WITHOUT_IMAGE_ID);
    insertPost(103L, "tie-low", AUTHOR_WITHOUT_IMAGE_ID);
    insertPost(104L, "oldest", AUTHOR_WITHOUT_IMAGE_ID);
    insertPost(105L, "other-user", AUTHOR_WITH_IMAGE_ID);

    insertBookmark(10L, REQUESTER_ID, 101L, NEWEST_BOOKMARKED_AT);
    insertBookmark(12L, REQUESTER_ID, 102L, TIED_BOOKMARKED_AT);
    insertBookmark(11L, REQUESTER_ID, 103L, TIED_BOOKMARKED_AT);
    insertBookmark(9L, REQUESTER_ID, 104L, OLDEST_BOOKMARKED_AT);
    insertBookmark(13L, OTHER_USER_ID, 105L, LocalDateTime.of(2026, 5, 6, 12, 0));
  }

  @Test
  @DisplayName("[getBookmarkedPosts] 북마크 생성일/ID 기준 cursor pagination")
  void getBookmarkedPosts_paginatesByBookmarkCreatedAtAndId() {
    var firstPage =
        paginationService.paginate(
            postBookmarkQueryRepository.getBookmarkedPosts(REQUESTER_ID),
            paginationProvider,
            new CursorPaginationRequest(2, null, null, null));

    assertThat(firstPage.nodes())
        .extracting(node -> node.content().slug())
        .containsExactly("newest", "tie-high");
    assertThat(firstPage.pageInfo().hasNextPage()).isTrue();
    assertThat(firstPage.pageInfo().hasPreviousPage()).isFalse();

    var secondPage =
        paginationService.paginate(
            postBookmarkQueryRepository.getBookmarkedPosts(REQUESTER_ID),
            paginationProvider,
            new CursorPaginationRequest(2, firstPage.pageInfo().endCursor(), null, null));

    assertThat(secondPage.nodes())
        .extracting(node -> node.content().slug())
        .containsExactly("tie-low", "oldest");
    assertThat(secondPage.pageInfo().hasNextPage()).isFalse();
    assertThat(secondPage.pageInfo().hasPreviousPage()).isTrue();

    var previousPage =
        paginationService.paginate(
            postBookmarkQueryRepository.getBookmarkedPosts(REQUESTER_ID),
            paginationProvider,
            new CursorPaginationRequest(null, null, 2, secondPage.pageInfo().startCursor()));

    assertThat(previousPage.nodes())
        .extracting(node -> node.content().slug())
        .containsExactly("newest", "tie-high");
    assertThat(previousPage.pageInfo().hasNextPage()).isTrue();
    assertThat(previousPage.pageInfo().hasPreviousPage()).isFalse();
  }

  @Test
  @DisplayName("[getBookmarkedPosts] 북마크 목록 응답 매핑")
  void getBookmarkedPosts_mapsBookmarkedPosts() throws Exception {
    var profileImageUrl = URI.create("https://example.com/profile.jpg").toURL();
    when(mediaDomainService.generatePublicGetUrls(List.of(PROFILE_IMAGE_ID)))
        .thenReturn(Map.of(PROFILE_IMAGE_ID, profileImageUrl));

    var response =
        postBookmarkQueryService.getBookmarkedPosts(
            REQUESTER_ID, new CursorPaginationRequest(2, null, null, null));

    var posts = response.posts().nodes();
    assertThat(posts)
        .extracting(node -> node.content().slug())
        .containsExactly("newest", "tie-high");
    assertThat(posts.get(0).content().bookmarked()).isTrue();
    assertThat(posts.get(0).content().bookmarkedAt()).isEqualTo(NEWEST_BOOKMARKED_AT);
    assertThat(posts.get(0).content().author().profileImageUrl())
        .isEqualTo(profileImageUrl.toExternalForm());
    assertThat(posts.get(1).content().author().profileImageUrl()).isNull();
    verify(mediaDomainService).generatePublicGetUrls(List.of(PROFILE_IMAGE_ID));
  }

  private void insertUser(
      Long id, String email, String handle, String fullName, Long profileImageId) {
    dsl.insertInto(USERS)
        .set(USERS.ID, id)
        .set(USERS.CREATED_AT, CREATED_AT)
        .set(USERS.UPDATED_AT, CREATED_AT)
        .set(USERS.EMAIL, email)
        .set(USERS.HANDLE, handle)
        .set(USERS.FULL_NAME, fullName)
        .set(USERS.PROFILE_IMAGE_ID, profileImageId)
        .execute();
  }

  private void insertMedia(Long id, Long uploaderId) {
    dsl.insertInto(MEDIA_FILES)
        .set(MEDIA_FILES.ID, id)
        .set(MEDIA_FILES.CREATED_AT, CREATED_AT)
        .set(MEDIA_FILES.UPDATED_AT, CREATED_AT)
        .set(MEDIA_FILES.UPLOADER_ID, uploaderId)
        .set(MEDIA_FILES.STATUS, "READY")
        .set(MEDIA_FILES.MEDIA_TYPE, "IMAGE")
        .set(MEDIA_FILES.BUCKET, "bucket")
        .set(MEDIA_FILES.KEY, "profile.jpg")
        .set(MEDIA_FILES.FILENAME, "profile.jpg")
        .set(MEDIA_FILES.SIZE, 100L)
        .execute();
  }

  private void updateProfileImage(Long userId, Long profileImageId) {
    dsl.update(USERS)
        .set(USERS.PROFILE_IMAGE_ID, profileImageId)
        .where(USERS.ID.eq(userId))
        .execute();
  }

  private void insertPost(Long id, String slug, Long authorId) {
    dsl.insertInto(POSTS)
        .set(POSTS.ID, id)
        .set(POSTS.CREATED_AT, CREATED_AT)
        .set(POSTS.UPDATED_AT, CREATED_AT)
        .set(POSTS.SLUG, slug)
        .set(POSTS.AUTHOR_ID, authorId)
        .set(POSTS.CONTENT, slug + " content")
        .execute();
  }

  private void insertBookmark(Long id, Long userId, Long postId, LocalDateTime bookmarkedAt) {
    dsl.insertInto(POST_BOOKMARKS)
        .set(POST_BOOKMARKS.ID, id)
        .set(POST_BOOKMARKS.CREATED_AT, bookmarkedAt)
        .set(POST_BOOKMARKS.UPDATED_AT, bookmarkedAt)
        .set(POST_BOOKMARKS.USER_ID, userId)
        .set(POST_BOOKMARKS.POST_ID, postId)
        .execute();
  }

  @TestConfiguration(proxyBeanMethods = false)
  static class TestConfig {

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    PostBookmarkMapper postBookmarkMapper() {
      return new PostBookmarkMapper() {};
    }

    @Bean
    MediaDomainService mediaDomainService() {
      return mock(MediaDomainService.class);
    }
  }
}
