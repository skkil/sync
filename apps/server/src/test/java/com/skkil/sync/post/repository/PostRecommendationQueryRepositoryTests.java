package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationContext;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.List;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, PostRecommendationQueryRepository.class})
class PostRecommendationQueryRepositoryTests {

  @Autowired private PostRecommendationQueryRepository postRecommendationQueryRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private TeammateRepository teammateRepository;

  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("[인기 게시글] 요청자가 팀원이어도 비공개 프로젝트 게시글은 탐색 후보에서 제외한다")
  void getDiscoveryCandidates_excludesPrivateProjectPostForTeammate() {
    User author = saveUser("trending-author");
    Project privateProject = saveProject("trending-private", false);
    Project publicProject = saveProject("trending-public", true);
    teammateRepository.saveAndFlush(Teammate.owner(privateProject, author));

    Post privatePost = savePost("trending-private-post", author, privateProject);
    Post publicPost = savePost("trending-public-post", author, publicProject);
    Post personalPost = savePost("trending-personal-post", author, null);

    List<PostRecommendationCandidate> candidates =
        getDiscoveryCandidates(new PostRecommendationContext(author.getId(), null));

    assertThat(candidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactlyInAnyOrder(publicPost.getId(), personalPost.getId())
        .doesNotContain(privatePost.getId());
  }

  @Test
  @DisplayName("[프로젝트 인기 게시글] 공개 프로젝트 게시글만 탐색 후보에 포함한다")
  void getDiscoveryCandidates_workspaceScopeIncludesOnlyPublicProjectPosts() {
    User author = saveUser("workspace-trending-author");
    Project privateProject = saveProject("workspace-trending-private", false);
    Project publicProject = saveProject("workspace-trending-public", true);
    teammateRepository.saveAndFlush(Teammate.owner(privateProject, author));

    Post privatePost = savePost("workspace-trending-private-post", author, privateProject);
    Post publicPost = savePost("workspace-trending-public-post", author, publicProject);
    Post personalPost = savePost("workspace-trending-personal-post", author, null);

    List<PostRecommendationCandidate> candidates =
        getDiscoveryCandidates(new PostRecommendationContext(author.getId(), PostScope.WORKSPACE));

    assertThat(candidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactly(publicPost.getId())
        .doesNotContain(privatePost.getId(), personalPost.getId());
  }

  private List<PostRecommendationCandidate> getDiscoveryCandidates(
      PostRecommendationContext context) {
    return postRecommendationQueryRepository
        .getDiscoveryCandidates(postRecommendationQueryRepository.trendingCondition(), context)
        .fetch(DSL.noCondition(), List.of(POSTS.LIKE_COUNT.desc(), POSTS.ID.desc()), 10);
  }

  private User saveUser(String key) {
    return userRepository.saveAndFlush(
        User.builder().email(key + "@example.com").fullName("사용자").build());
  }

  private Project saveProject(String handle, boolean isPublic) {
    return projectRepository.saveAndFlush(
        Project.builder().handle(handle).name(handle).isPublic(isPublic).build());
  }

  private Post savePost(String slug, User author, @Nullable Project project) {
    Post post =
        Post.builder()
            .slug(slug)
            .author(author)
            .project(project)
            .type(PostType.SHORT)
            .status(PostStatus.PUBLISHED)
            .content("본문")
            .build();
    post.updateContent("본문", "본문", 0);

    return postRepository.saveAndFlush(post);
  }
}
