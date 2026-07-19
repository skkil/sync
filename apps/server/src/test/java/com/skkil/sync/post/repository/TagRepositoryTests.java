package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, PostQueryRepository.class})
class TagRepositoryTests {

  @Autowired private TagRepository tagRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private PostQueryRepository postQueryRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EntityManager entityManager;

  @Test
  @DisplayName("공개 발행 글에 연결된 미인증 태그는 검색되고 게시글 조회에 포함된다")
  void searchTags_unverifiedTagWithPublicPublishedPost_returnsTagAndPostRelation() {
    Post savedPost = savePostWithTag("태그1", PostStatus.PUBLISHED, PostType.SHORT);

    entityManager.flush();
    entityManager.clear();

    var tags = tagRepository.searchTags("태그1", PostStatus.PUBLISHED, PostVisibility.VISIBLE);
    var taggedPosts =
        postQueryRepository
            .getPostsByTag(null, savedPost.getTags().getFirst().getTag().getId(), null)
            .fetch(DSL.noCondition(), List.of(POSTS.ID.asc()), 10);

    assertThat(tags)
        .singleElement()
        .satisfies(
            tag -> {
              assertThat(tag.getName()).isEqualTo("태그1");
              assertThat(tag.isVerified()).isFalse();
              assertThat(tag.getPostCount()).isEqualTo(1L);
            });
    assertThat(taggedPosts)
        .singleElement()
        .satisfies(taggedPost -> assertThat(taggedPost.slug()).isEqualTo(savedPost.getSlug()));
  }

  @Test
  @DisplayName("미인증 태그가 임시 저장 글에만 연결된 경우 검색되지 않는다")
  void searchTags_unverifiedTagWithDraftPost_returnsEmpty() {
    Post savedPost = savePostWithTag("임시태그", PostStatus.DRAFT, PostType.SHORT);

    entityManager.flush();
    entityManager.clear();

    var tags = tagRepository.searchTags("임시태그", PostStatus.PUBLISHED, PostVisibility.VISIBLE);
    var taggedPosts =
        postQueryRepository
            .getPostsByTag(null, savedPost.getTags().getFirst().getTag().getId(), null)
            .fetch(DSL.noCondition(), List.of(POSTS.ID.asc()), 10);

    assertThat(tags).isEmpty();
    assertThat(taggedPosts).isEmpty();
  }

  @Test
  @DisplayName("미인증 태그가 숨김 글에만 연결된 경우 검색되지 않는다")
  void searchTags_unverifiedTagWithHiddenPost_returnsEmpty() {
    Post post = savePostWithTag("숨김태그", PostStatus.PUBLISHED, PostType.SHORT);
    post.hide(post.getAuthor(), "신고 처리");

    entityManager.flush();
    entityManager.clear();

    var tags = tagRepository.searchTags("숨김태그", PostStatus.PUBLISHED, PostVisibility.VISIBLE);
    var taggedPosts =
        postQueryRepository
            .getPostsByTag(null, post.getTags().getFirst().getTag().getId(), null)
            .fetch(DSL.noCondition(), List.of(POSTS.ID.asc()), 10);

    assertThat(tags).isEmpty();
    assertThat(taggedPosts).isEmpty();
  }

  @Test
  @DisplayName("인증된 태그는 연결된 글이 없어도 검색된다")
  void searchTags_verifiedTagWithoutPost_returnsTag() {
    Tag tag = Tag.builder().name("공식태그").build();
    tag.verify();
    tagRepository.saveAndFlush(tag);

    entityManager.clear();

    var tags = tagRepository.searchTags("공식태그", PostStatus.PUBLISHED, PostVisibility.VISIBLE);

    assertThat(tags).extracting(Tag::getName).containsExactly("공식태그");
  }

  @Test
  @DisplayName("태그 게시글 조회 시 게시글 타입으로 필터링할 수 있다")
  void getPostsByTag_withType_returnsMatchingPostsOnly() {
    Post shortPost = savePostWithTag("필터태그", PostStatus.PUBLISHED, PostType.SHORT);
    Post longPost = savePostWithTag("필터태그", PostStatus.PUBLISHED, PostType.LONG);

    entityManager.flush();
    entityManager.clear();

    var taggedPosts =
        postQueryRepository
            .getPostsByTag(null, shortPost.getTags().getFirst().getTag().getId(), PostType.LONG)
            .fetch(DSL.noCondition(), List.of(POSTS.ID.asc()), 10);

    assertThat(taggedPosts).extracting(post -> post.slug()).containsExactly(longPost.getSlug());
  }

  private Post savePostWithTag(String tagName, PostStatus status, PostType type) {
    User author =
        userRepository.save(
            User.builder()
                .email(tagName + "-" + status + "-" + type + "@example.com")
                .fullName("태그 작성자")
                .build());
    Tag tag =
        tagRepository
            .findByNameAndProjectIsNull(tagName)
            .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
    Post post =
        Post.builder()
            .slug(tagName + "-" + status + "-" + type)
            .author(author)
            .type(type)
            .status(status)
            .content("태그가 포함된 게시글")
            .build();
    post.updateContent("태그가 포함된 게시글", "태그가 포함된 게시글", 0);
    post.addTag(PostTag.builder().post(post).tag(tag).build());
    Post savedPost = postRepository.save(post);
    tagRepository.incrementPostCount(tag);
    return savedPost;
  }
}
