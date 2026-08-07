package com.skkil.sync.user.repository;

import static com.skkil.sync.jooq.tables.Users.USERS;
import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.ProjectFollowRelationship;
import com.skkil.sync.project.repository.ProjectFollowRelationshipRepository;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserFollowRelationship;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, UserRecommendationQueryRepository.class})
class UserRecommendationQueryRepositoryTests {

  @Autowired private UserRecommendationQueryRepository userRecommendationQueryRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private UserFollowRelationshipRepository userFollowRelationshipRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private ProjectFollowRelationshipRepository projectFollowRelationshipRepository;

  @Autowired private DSLContext dsl;

  @Test
  @DisplayName("[프로젝트 중복 추천] 유효한 사용자만 같은 프로젝트 팔로우 수와 ID 순으로 반환한다")
  void findProjectOverlapCandidateIds_returnsOnlyRecommendableUsersInStableOrder() {
    User requester = saveOnboardedUser("overlap-requester");
    User first = saveOnboardedUser("overlap-first");
    User second = saveOnboardedUser("overlap-second");
    User notOnboarded = saveNotOnboardedUser("overlap-not-onboarded");
    User missingHandle = saveUserWithoutHandle("missing-handle-overlap");
    User deleted = saveOnboardedUser("overlap-deleted");
    Project project = saveProject("overlap-project");

    followProject(requester, project);
    followProject(first, project);
    followProject(second, project);
    followProject(notOnboarded, project);
    followProject(missingHandle, project);
    followProject(deleted, project);
    markOnboardedWithoutHandle(missingHandle);
    markDeleted(deleted);

    assertThat(
            userRecommendationQueryRepository.findProjectOverlapCandidateIds(requester.getId(), 20))
        .containsExactly(second.getId(), first.getId());
  }

  @Test
  @DisplayName("[인기 사용자 추천] 유효한 사용자만 최근 팔로우 수와 ID 순으로 반환한다")
  void findTrendingCandidateIds_returnsOnlyRecommendableUsersInStableOrder() {
    User requester = saveOnboardedUser("trending-requester");
    User follower = saveOnboardedUser("trending-follower");
    User first = saveOnboardedUser("trending-first");
    User second = saveOnboardedUser("trending-second");
    User notOnboarded = saveNotOnboardedUser("trending-not-onboarded");
    User missingHandle = saveUserWithoutHandle("missing-handle-trending");
    User deleted = saveOnboardedUser("trending-deleted");

    followUser(follower, first);
    followUser(follower, second);
    followUser(follower, notOnboarded);
    followUser(follower, missingHandle);
    followUser(follower, deleted);
    markOnboardedWithoutHandle(missingHandle);
    markDeleted(deleted);

    assertThat(userRecommendationQueryRepository.findTrendingCandidateIds(requester.getId(), 20))
        .containsExactly(second.getId(), first.getId());
  }

  @Test
  @DisplayName("[최근 가입 추천] 유효하고 아직 팔로우하지 않은 사용자를 생성 시각과 ID 순으로 반환한다")
  void findRecentlyJoinedCandidateIds_returnsOnlyUnfollowedRecommendableUsersInStableOrder() {
    User requester = saveOnboardedUser("recent-requester");
    User first = saveOnboardedUser("recent-first");
    User second = saveOnboardedUser("recent-second");
    User alreadyFollowing = saveOnboardedUser("recent-already-following");
    User notOnboarded = saveNotOnboardedUser("recent-not-onboarded");
    User missingHandle = saveUserWithoutHandle("missing-handle-recent");
    User deleted = saveOnboardedUser("recent-deleted");

    followUser(requester, alreadyFollowing);
    markOnboardedWithoutHandle(missingHandle);
    markDeleted(deleted);
    setSameCreatedAt(
        List.of(first, second, alreadyFollowing, notOnboarded, missingHandle, deleted));

    assertThat(
            userRecommendationQueryRepository.findRecentlyJoinedCandidateIds(requester.getId(), 20))
        .containsExactly(second.getId(), first.getId());
  }

  private User saveOnboardedUser(String key) {
    User user = saveNotOnboardedUser(key);
    user.onboard();
    return userRepository.saveAndFlush(user);
  }

  private User saveNotOnboardedUser(String key) {
    User user = User.builder().email(key + "@example.com").fullName("추천 사용자").build();
    user.updateHandle(key);
    return userRepository.saveAndFlush(user);
  }

  private User saveUserWithoutHandle(String key) {
    return userRepository.saveAndFlush(
        User.builder().email(key + "@example.com").fullName("추천 사용자").build());
  }

  private Project saveProject(String handle) {
    return projectRepository.saveAndFlush(
        Project.builder().handle(handle).name(handle).isPublic(true).build());
  }

  private void followProject(User follower, Project project) {
    projectFollowRelationshipRepository.saveAndFlush(
        ProjectFollowRelationship.builder().follower(follower).project(project).build());
  }

  private void followUser(User follower, User followee) {
    userFollowRelationshipRepository.saveAndFlush(
        UserFollowRelationship.builder().follower(follower).followee(followee).build());
  }

  private void markOnboardedWithoutHandle(User user) {
    dsl.update(USERS).set(USERS.IS_ONBOARDED, true).where(USERS.ID.eq(user.getId())).execute();
  }

  private void markDeleted(User user) {
    dsl.update(USERS)
        .set(USERS.DELETED_AT, OffsetDateTime.now(ZoneOffset.UTC))
        .where(USERS.ID.eq(user.getId()))
        .execute();
  }

  private void setSameCreatedAt(List<User> users) {
    OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(1);
    dsl.update(USERS)
        .set(USERS.CREATED_AT, createdAt)
        .where(USERS.ID.in(users.stream().map(User::getId).toList()))
        .execute();
  }
}
