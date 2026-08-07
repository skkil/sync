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
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.model.TagFollowRelationship;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.ProjectFollowRelationship;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectFollowRelationshipRepository;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserFollowRelationship;
import com.skkil.sync.user.repository.UserFollowRelationshipRepository;
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

  @Autowired private PostTagRepository postTagRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private TagFollowRelationshipRepository tagFollowRelationshipRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private ProjectFollowRelationshipRepository projectFollowRelationshipRepository;

  @Autowired private TeammateRepository teammateRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private UserFollowRelationshipRepository userFollowRelationshipRepository;

  @Test
  @DisplayName("[팔로잉 게시글] 사용자·프로젝트·전역 태그로 팔로우한 게시글을 중복 없이 조회한다")
  void getFollowingCandidates_includesUserProjectAndGlobalTagMatchesWithoutDuplicates() {
    User requester = saveUser("following-requester");
    User followedAuthor = saveUser("following-author");
    User unrelatedAuthor = saveUser("following-unrelated-author");
    Project followedProject = saveProject("following-project", true);
    Tag followedTag = saveTag("following-global-tag", null);

    userFollowRelationshipRepository.saveAndFlush(
        UserFollowRelationship.builder().follower(requester).followee(followedAuthor).build());
    projectFollowRelationshipRepository.saveAndFlush(
        ProjectFollowRelationship.builder().follower(requester).project(followedProject).build());
    followTag(requester, followedTag);

    Post userPost = savePost("following-user-post", followedAuthor, null);
    Post projectPost = savePost("following-project-post", unrelatedAuthor, followedProject);
    Post tagPost = savePost("following-tag-post", unrelatedAuthor, null);
    addTag(tagPost, followedTag);

    Post allConditionsPost =
        savePost("following-all-conditions-post", followedAuthor, followedProject);
    addTag(allConditionsPost, followedTag);

    Post unrelatedPost = savePost("following-unrelated-post", unrelatedAuthor, null);

    List<PostRecommendationCandidate> candidates =
        getFollowingCandidates(new PostRecommendationContext(requester.getId(), null, null));

    assertThat(candidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactlyInAnyOrder(
            userPost.getId(), projectPost.getId(), tagPost.getId(), allConditionsPost.getId())
        .doesNotContain(unrelatedPost.getId());
  }

  @Test
  @DisplayName("[팔로잉 게시글] 프로젝트 전용 태그 팔로우 데이터는 후보 조건으로 사용하지 않는다")
  void getFollowingCandidates_ignoresProjectTagFollowRelationship() {
    User requester = saveUser("project-tag-requester");
    User author = saveUser("project-tag-author");
    Project project = saveProject("project-tag-project", true);
    Tag projectTag = saveTag("project-only-tag", project);
    followTag(requester, projectTag);

    Post post = savePost("project-tag-post", author, project);
    addTag(post, projectTag);

    List<PostRecommendationCandidate> candidates =
        getFollowingCandidates(new PostRecommendationContext(requester.getId(), null, null));

    assertThat(candidates).extracting(PostRecommendationCandidate::id).doesNotContain(post.getId());
  }

  @Test
  @DisplayName("[팔로잉 게시글] 팔로우 태그가 같아도 비공개 프로젝트 게시글은 팀원에게만 노출한다")
  void getFollowingCandidates_privateProjectTagPostRequiresMembership() {
    User author = saveUser("private-tag-author");
    User teammate = saveUser("private-tag-teammate");
    User outsider = saveUser("private-tag-outsider");
    Project privateProject = saveProject("private-tag-project", false);
    Tag followedTag = saveTag("private-project-global-tag", null);

    teammateRepository.saveAndFlush(Teammate.owner(privateProject, author));
    teammateRepository.saveAndFlush(Teammate.member(privateProject, teammate));
    followTag(teammate, followedTag);
    followTag(outsider, followedTag);

    Post privatePost = savePost("private-tag-post", author, privateProject);
    addTag(privatePost, followedTag);

    List<PostRecommendationCandidate> teammateCandidates =
        getFollowingCandidates(new PostRecommendationContext(teammate.getId(), null, null));
    List<PostRecommendationCandidate> outsiderCandidates =
        getFollowingCandidates(new PostRecommendationContext(outsider.getId(), null, null));

    assertThat(teammateCandidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactly(privatePost.getId());
    assertThat(outsiderCandidates)
        .extracting(PostRecommendationCandidate::id)
        .doesNotContain(privatePost.getId());
  }

  @Test
  @DisplayName("[팔로잉 게시글] scope 필터로 개인 게시글과 프로젝트 게시글을 구분한다")
  void getFollowingCandidates_filtersByScope() {
    User requester = saveUser("following-scope-requester");
    User author = saveUser("following-scope-author");
    Project project = saveProject("following-scope-project", true);
    Tag followedTag = saveTag("following-scope-tag", null);
    followTag(requester, followedTag);

    Post personalPost = savePost("following-scope-personal", author, null);
    Post projectPost = savePost("following-scope-workspace", author, project);
    addTag(personalPost, followedTag);
    addTag(projectPost, followedTag);

    List<PostRecommendationCandidate> personalCandidates =
        getFollowingCandidates(
            new PostRecommendationContext(requester.getId(), PostScope.PUBLIC, null));
    List<PostRecommendationCandidate> projectCandidates =
        getFollowingCandidates(
            new PostRecommendationContext(requester.getId(), PostScope.WORKSPACE, null));

    assertThat(personalCandidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactly(personalPost.getId());
    assertThat(projectCandidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactly(projectPost.getId());
  }

  @Test
  @DisplayName("[팔로잉 게시글] postType 필터와 일치하는 형태의 게시글만 조회한다")
  void getFollowingCandidates_filtersByPostType() {
    User requester = saveUser("following-type-requester");
    User author = saveUser("following-type-author");
    Tag followedTag = saveTag("following-type-tag", null);
    followTag(requester, followedTag);

    Post shortPost = savePost("following-short-post", author, null, PostType.SHORT);
    Post longPost = savePost("following-long-post", author, null, PostType.LONG);
    Post questionPost = savePost("following-question-post", author, null, PostType.QUESTION);
    addTag(shortPost, followedTag);
    addTag(longPost, followedTag);
    addTag(questionPost, followedTag);

    List<PostRecommendationCandidate> candidates =
        getFollowingCandidates(
            new PostRecommendationContext(requester.getId(), null, PostType.QUESTION));

    assertThat(candidates)
        .extracting(PostRecommendationCandidate::id)
        .containsExactly(questionPost.getId())
        .doesNotContain(shortPost.getId(), longPost.getId());
  }

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
        getDiscoveryCandidates(new PostRecommendationContext(author.getId(), null, null));

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
        getDiscoveryCandidates(
            new PostRecommendationContext(author.getId(), PostScope.WORKSPACE, null));

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

  private List<PostRecommendationCandidate> getFollowingCandidates(
      PostRecommendationContext context) {
    return postRecommendationQueryRepository
        .getCandidates(
            postRecommendationQueryRepository.followingCondition(context.requesterId()), context)
        .fetch(DSL.noCondition(), List.of(POSTS.CREATED_AT.desc(), POSTS.ID.desc()), 50);
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
    return savePost(slug, author, project, PostType.SHORT);
  }

  private Post savePost(String slug, User author, @Nullable Project project, PostType postType) {
    Post post =
        Post.builder()
            .slug(slug)
            .author(author)
            .project(project)
            .title(postType == PostType.SHORT ? null : "제목")
            .type(postType)
            .status(PostStatus.PUBLISHED)
            .content("본문")
            .build();
    post.updateContent("본문", "본문", 0);

    return postRepository.saveAndFlush(post);
  }

  private Tag saveTag(String name, @Nullable Project project) {
    return tagRepository.saveAndFlush(Tag.builder().name(name).project(project).build());
  }

  private void addTag(Post post, Tag tag) {
    postTagRepository.saveAndFlush(PostTag.builder().post(post).tag(tag).build());
  }

  private void followTag(User follower, Tag tag) {
    tagFollowRelationshipRepository.saveAndFlush(
        TagFollowRelationship.builder().follower(follower).tag(tag).build());
  }
}
