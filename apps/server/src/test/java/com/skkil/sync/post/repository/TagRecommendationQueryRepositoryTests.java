package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Tags.TAGS;
import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.model.TagFollowRelationship;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.DSLContext;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, TagRecommendationQueryRepository.class})
class TagRecommendationQueryRepositoryTests {

  @Autowired private TagRecommendationQueryRepository tagRecommendationQueryRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private PostTagRepository postTagRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private TagFollowRelationshipRepository tagFollowRelationshipRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private DSLContext dsl;

  @Test
  @DisplayName("[인기 태그 추천] 개인 및 공개 프로젝트 게시글만 집계하고 동률이면 ID 역순으로 반환한다")
  void findTrendingCandidateIds_countsOnlyPublicFeedPostsInStableOrder() {
    User requester = saveUser("tag-requester");
    User author = saveUser("tag-author");
    Project publicProject = saveProject("public-tag-project", true);
    Project privateProject = saveProject("private-tag-project", false);

    Tag personalTag = saveTag("personal-tag", null, true);
    Tag publicProjectPostTag = saveTag("public-project-post-tag", null, true);
    Tag privateProjectPostTag = saveTag("private-project-post-tag", null, true);
    Tag draftPostTag = saveTag("draft-post-tag", null, true);
    Tag hiddenPostTag = saveTag("hidden-post-tag", null, true);
    Tag oldPostTag = saveTag("old-post-tag", null, true);
    Tag followedTag = saveTag("followed-tag", null, true);
    Tag unverifiedTag = saveTag("unverified-tag", null, false);
    Tag projectTag = saveTag("project-tag", publicProject, true);

    attachTag(savePost("personal-tag-post", author, null, PostStatus.PUBLISHED), personalTag);
    attachTag(
        savePost("public-project-tag-post", author, publicProject, PostStatus.PUBLISHED),
        publicProjectPostTag);
    attachTag(
        savePost("private-project-tag-post", author, privateProject, PostStatus.PUBLISHED),
        privateProjectPostTag);
    attachTag(savePost("draft-tag-post", author, null, PostStatus.DRAFT), draftPostTag);

    Post hiddenPost = savePost("hidden-tag-post", author, null, PostStatus.PUBLISHED);
    hiddenPost.hide(author, "추천 집계 제외");
    postRepository.saveAndFlush(hiddenPost);
    attachTag(hiddenPost, hiddenPostTag);

    Post oldPost = savePost("old-tag-post", author, null, PostStatus.PUBLISHED);
    attachTag(oldPost, oldPostTag);
    markPostAsOld(oldPost);

    attachTag(savePost("followed-tag-post", author, null, PostStatus.PUBLISHED), followedTag);
    tagFollowRelationshipRepository.saveAndFlush(
        TagFollowRelationship.builder().follower(requester).tag(followedTag).build());

    attachTag(savePost("unverified-tag-post", author, null, PostStatus.PUBLISHED), unverifiedTag);
    attachTag(
        savePost("project-scoped-tag-post", author, publicProject, PostStatus.PUBLISHED),
        projectTag);

    assertThat(tagRecommendationQueryRepository.findTrendingCandidateIds(requester.getId(), 20))
        .containsExactly(publicProjectPostTag.getId(), personalTag.getId());
  }

  @Test
  @DisplayName("[최근 태그 추천] 검증된 전역 태그 중 아직 팔로우하지 않은 태그를 안정적으로 반환한다")
  void findRecentlyCreatedCandidateIds_returnsOnlyUnfollowedGlobalVerifiedTagsInStableOrder() {
    User requester = saveUser("recent-tag-requester");
    Project project = saveProject("recent-tag-project", true);
    Tag first = saveTag("recent-first", null, true);
    Tag second = saveTag("recent-second", null, true);
    Tag alreadyFollowing = saveTag("recent-following", null, true);
    Tag unverified = saveTag("recent-unverified", null, false);
    Tag projectTag = saveTag("recent-project-tag", project, true);

    tagFollowRelationshipRepository.saveAndFlush(
        TagFollowRelationship.builder().follower(requester).tag(alreadyFollowing).build());
    setSameCreatedAt(List.of(first, second, alreadyFollowing, unverified, projectTag));

    assertThat(
            tagRecommendationQueryRepository.findRecentlyCreatedCandidateIds(requester.getId(), 20))
        .containsExactly(second.getId(), first.getId());
  }

  private User saveUser(String key) {
    return userRepository.saveAndFlush(
        User.builder().email(key + "@example.com").fullName("태그 추천 사용자").build());
  }

  private Project saveProject(String handle, boolean isPublic) {
    return projectRepository.saveAndFlush(
        Project.builder().handle(handle).name(handle).isPublic(isPublic).build());
  }

  private Tag saveTag(String name, @Nullable Project project, boolean verified) {
    Tag tag = Tag.builder().name(name).description(name).project(project).build();
    if (verified) {
      tag.verify();
    }
    return tagRepository.saveAndFlush(tag);
  }

  private Post savePost(String slug, User author, @Nullable Project project, PostStatus status) {
    Post post =
        Post.builder()
            .slug(slug)
            .author(author)
            .project(project)
            .type(PostType.SHORT)
            .status(status)
            .content("본문")
            .build();
    post.updateContent("본문", "본문", 0);
    return postRepository.saveAndFlush(post);
  }

  private void attachTag(Post post, Tag tag) {
    postTagRepository.saveAndFlush(PostTag.builder().post(post).tag(tag).build());
  }

  private void markPostAsOld(Post post) {
    dsl.update(POSTS)
        .set(POSTS.CREATED_AT, OffsetDateTime.now(ZoneOffset.UTC).minusDays(8))
        .where(POSTS.ID.eq(post.getId()))
        .execute();
  }

  private void setSameCreatedAt(List<Tag> tags) {
    OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(1);
    dsl.update(TAGS)
        .set(TAGS.CREATED_AT, createdAt)
        .where(TAGS.ID.in(tags.stream().map(Tag::getId).toList()))
        .execute();
  }
}
