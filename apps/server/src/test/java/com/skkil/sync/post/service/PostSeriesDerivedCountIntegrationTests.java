package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.post.dto.request.AddPostToPostSeriesRequest;
import com.skkil.sync.post.dto.response.GetPostSeriesListResponse;
import com.skkil.sync.post.dto.response.GetPostSeriesResponse;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostSeries;
import com.skkil.sync.post.model.PostSeriesPost;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.repository.PostSeriesPostRepository;
import com.skkil.sync.post.repository.PostSeriesRepository;
import com.skkil.sync.user.constant.Role;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 시리즈 편 수와 순번이 post_series_posts 행에서 파생되는지 검증한다. 과거에는 post_series 에 저장된 카운터를 썼는데, 게시글 삭제가 FK
 * cascade 로만 편성 행을 지워 카운터가 감소하지 않고 부풀어 남는 버그가 있었다(추가 누적 6 vs 실제 3). 파생 방식에서는 어떤 삭제 경로든 행이 사라지는 즉시
 * 값이 맞는다.
 */
@Import(TestcontainersConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(properties = "app.seed.enabled=false")
@Transactional
class PostSeriesDerivedCountIntegrationTests {

  @Autowired private PostSeriesService seriesService;
  @Autowired private PostService postService;

  @Autowired private UserRepository userRepository;
  @Autowired private PostRepository postRepository;
  @Autowired private PostSeriesRepository seriesRepository;
  @Autowired private PostSeriesPostRepository seriesPostRepository;

  @Autowired private EntityManager entityManager;

  private User user;
  private PostSeries series;
  private List<Post> posts;
  private long unique;

  @BeforeEach
  void setUp() {
    unique = System.nanoTime();
    user =
        userRepository.save(
            User.builder()
                .email("series-tester-" + unique + "@example.com")
                .fullName("Series Tester")
                .build());
    authenticate(user);

    series =
        seriesRepository.save(
            PostSeries.builder().externalId("series-" + unique).creator(user).name("ps").build());

    posts = new ArrayList<>();
    for (int i = 1; i <= 3; i++) {
      posts.add(saveAndAttachPost("series-post-" + unique + "-" + i, PostStatus.PUBLISHED));
    }
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("[getMyPersonalSeries] postCount 는 실제 편 수에서 파생된다")
  void postCountIsDerivedFromRows() {
    assertThat(myPersonalSeriesPostCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("[deletePost] 전부 지우고 다시 채워도 편 수가 누적되지 않는다")
  void postCountDoesNotAccumulateAcrossDeletes() {
    for (Post post : posts) {
      postService.deletePost(post.getId());
    }
    assertThat(myPersonalSeriesPostCount()).isZero();

    for (int i = 1; i <= 3; i++) {
      saveAndAttachPost("series-post-again-" + unique + "-" + i, PostStatus.PUBLISHED);
    }
    entityManager.flush();
    entityManager.clear();

    // 저장 카운터였다면 6 으로 부풀었을 값이다.
    assertThat(myPersonalSeriesPostCount()).isEqualTo(3);
    assertThat(seriesPostRepository.findBySeriesIdOrderByPositionAscIdAsc(series.getId()))
        .extracting(PostSeriesPost::getPosition)
        .containsExactly(1, 2, 3);
  }

  @Test
  @DisplayName("[deletePost] 가운데 편을 지우면 편 수와 순번이 함께 정리된다")
  void deletePostRemovesMembershipAndClosesPositionGap() {
    postService.deletePost(posts.get(1).getId());
    entityManager.flush();
    entityManager.clear();

    assertThat(myPersonalSeriesPostCount()).isEqualTo(2);

    List<PostSeriesPost> remaining =
        seriesPostRepository.findBySeriesIdOrderByPositionAscIdAsc(series.getId());
    assertThat(remaining).extracting(PostSeriesPost::getPosition).containsExactly(1, 2);
    assertThat(remaining)
        .extracting(item -> item.getPost().getId())
        .containsExactly(posts.get(0).getId(), posts.get(2).getId());

    GetPostSeriesResponse forPost = seriesService.getSeriesForPost(posts.get(0).getSlug(), null);
    assertThat(forPost.series()).isNotNull();
    assertThat(forPost.series().postCount()).isEqualTo(2);
    assertThat(forPost.posts())
        .extracting(GetPostSeriesResponse.Post::position)
        .containsExactly(1, 2);
  }

  @Test
  @DisplayName("[deletePost] 시리즈에 편성된 초안을 지워도 편 수가 정리된다")
  void deletingDraftInSeriesCleansUpCount() {
    Post draft = saveAndAttachPost("series-draft-" + unique, PostStatus.DRAFT);
    assertThat(myPersonalSeriesPostCount()).isEqualTo(4);

    postService.deletePost(draft.getId());
    entityManager.flush();
    entityManager.clear();

    assertThat(myPersonalSeriesPostCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("[removeItem] 기존 편성 해제 경로도 편 수와 순번을 동일하게 정리한다")
  void removeItemStaysConsistentWithDerivedCount() {
    PostSeriesPost seriesPost =
        seriesPostRepository.findByPostId(posts.get(0).getId()).orElseThrow();

    seriesService.removeItem(series.getExternalId(), seriesPost.getId());
    entityManager.flush();
    entityManager.clear();

    assertThat(myPersonalSeriesPostCount()).isEqualTo(2);
    assertThat(seriesPostRepository.findBySeriesIdOrderByPositionAscIdAsc(series.getId()))
        .extracting(PostSeriesPost::getPosition)
        .containsExactly(1, 2);
  }

  private long myPersonalSeriesPostCount() {
    GetPostSeriesListResponse response = seriesService.getMyPersonalSeries(user.getId());
    assertThat(response.series()).hasSize(1);
    return response.series().get(0).postCount();
  }

  private Post saveAndAttachPost(String slug, PostStatus status) {
    Post post =
        Post.builder()
            .slug(slug)
            .author(user)
            .title("시리즈 편 " + slug)
            .type(PostType.LONG)
            .status(status)
            .jsonContent("본문")
            .build();
    post.updateJsonContent("본문", "본문", 0);
    post = postRepository.saveAndFlush(post);

    seriesService.addPost(
        user.getId(),
        series.getExternalId(),
        new AddPostToPostSeriesRequest(null, post.getSlug(), null));
    return post;
  }

  private void authenticate(User user) {
    AuthenticatedUser principal =
        new AuthenticatedUser(
            user.getId(), user.getFullName(), user.getEmail(), null, Role.USER, true);
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities()));
  }
}
