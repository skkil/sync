package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.service.TagService;
import com.skkil.sync.project.service.ProjectDomainService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, PostQueryRepository.class, TagService.class})
@MockitoBean(types = {ProjectDomainService.class, TagMapper.class})
class TagRepositoryTests {

  @Autowired private TagRepository tagRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private PostQueryRepository postQueryRepository;

  @Autowired private TagService tagService;

  @Autowired private UserRepository userRepository;

  @Autowired private EntityManager entityManager;

  @Test
  @DisplayName("공개 발행 글에 연결된 미인증 태그는 검색되고 게시글 조회에 포함된다")
  void searchTags_unverifiedTagWithPublicPublishedPost_returnsTagAndPostRelation() {
    savePostWithTag("태그1", PostStatus.PUBLISHED);

    entityManager.flush();
    entityManager.clear();

    var tags = tagRepository.searchTags("태그1", PostStatus.PUBLISHED, PostVisibility.VISIBLE);
    var post = postQueryRepository.getPostBySlug(null, "태그1-post").orElseThrow();
    var taggedPosts =
        postQueryRepository
            .getPublicPostsByTag(null, "태그1", null)
            .fetch(DSL.noCondition(), List.of(POSTS.ID.asc()), 10);

    assertThat(tags)
        .singleElement()
        .satisfies(
            tag -> {
              assertThat(tag.getName()).isEqualTo("태그1");
              assertThat(tag.isVerified()).isFalse();
              assertThat(tag.getPostCount()).isEqualTo(1L);
            });
    assertThat(post.tags()).containsExactly("태그1");
    assertThat(taggedPosts)
        .singleElement()
        .satisfies(taggedPost -> assertThat(taggedPost.tags()).containsExactly("태그1"));
  }

  @Test
  @DisplayName("미인증 태그가 임시 저장 글에만 연결된 경우 검색되지 않는다")
  void searchTags_unverifiedTagWithDraftPost_returnsEmpty() {
    savePostWithTag("임시태그", PostStatus.DRAFT);

    entityManager.flush();
    entityManager.clear();

    var tags = tagRepository.searchTags("임시태그", PostStatus.PUBLISHED, PostVisibility.VISIBLE);
    var taggedPosts =
        postQueryRepository
            .getPublicPostsByTag(null, "임시태그", null)
            .fetch(DSL.noCondition(), List.of(POSTS.ID.asc()), 10);

    assertThat(tags).isEmpty();
    assertThat(taggedPosts).isEmpty();
  }

  @Test
  @DisplayName("미인증 태그가 숨김 글에만 연결된 경우 검색되지 않는다")
  void searchTags_unverifiedTagWithHiddenPost_returnsEmpty() {
    Post post = savePostWithTag("숨김태그", PostStatus.PUBLISHED);
    post.hide(post.getAuthor(), "신고 처리");

    entityManager.flush();
    entityManager.clear();

    var tags = tagRepository.searchTags("숨김태그", PostStatus.PUBLISHED, PostVisibility.VISIBLE);
    var taggedPosts =
        postQueryRepository
            .getPublicPostsByTag(null, "숨김태그", null)
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
  @DisplayName("태그가 없는 글은 빈 태그 목록으로 조회된다")
  void getPostBySlug_postWithoutTags_returnsEmptyTags() {
    User author =
        userRepository.save(
            User.builder().email("no-tags@example.com").fullName("태그 없는 작성자").build());
    postRepository.save(
        Post.builder()
            .slug("no-tags-post")
            .author(author)
            .type(PostType.SHORT)
            .status(PostStatus.PUBLISHED)
            .content("태그가 없는 게시글")
            .build());

    entityManager.flush();
    entityManager.clear();

    var post = postQueryRepository.getPostBySlug(null, "no-tags-post").orElseThrow();

    assertThat(post.tags()).isEmpty();
  }

  private Post savePostWithTag(String tagName, PostStatus status) {
    User author =
        userRepository.save(
            User.builder().email(tagName + "@example.com").fullName("태그 작성자").build());
    Post post =
        Post.builder()
            .slug(tagName + "-post")
            .author(author)
            .type(PostType.SHORT)
            .status(status)
            .content("태그가 포함된 게시글")
            .build();
    tagService.addTagsToPost(post, null, List.of(tagName));
    return postRepository.save(post);
  }
}
