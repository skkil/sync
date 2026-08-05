package com.skkil.sync.project.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.project.dto.data.MyProjectDto;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.ProjectFollowRelationship;
import com.skkil.sync.project.model.Role;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, ProjectQueryRepository.class})
class ProjectQueryRepositoryTests {

  @Autowired private ProjectQueryRepository projectQueryRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private ProjectFollowRelationshipRepository projectFollowRelationshipRepository;

  @Autowired private TeammateRepository teammateRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("[getMyProjects] 실제 역할, 소유 여부, 멤버 수와 미해결 질문 수를 프로젝트별로 집계한다")
  void getMyProjects_returnsMembershipAndAggregates() {
    User requester = saveUser("project-list-requester");
    User teammate = saveUser("project-list-teammate");
    User outsider = saveUser("project-list-outsider");

    Project privateProject = saveProject("private-project-list", false);
    Project publicProject = saveProject("public-project-list", true);
    Project unrelatedProject = saveProject("unrelated-project-list", false);

    teammateRepository.saveAndFlush(Teammate.owner(privateProject, requester));
    teammateRepository.saveAndFlush(Teammate.member(privateProject, teammate));
    teammateRepository.saveAndFlush(Teammate.member(publicProject, requester));
    Teammate publicAdmin = Teammate.member(publicProject, teammate);
    publicAdmin.setRole(Role.ADMIN);
    teammateRepository.saveAndFlush(publicAdmin);
    teammateRepository.saveAndFlush(Teammate.owner(unrelatedProject, outsider));

    savePost(
        "visible-unresolved-question",
        requester,
        privateProject,
        PostType.QUESTION,
        PostStatus.PUBLISHED);

    Post resolvedQuestion =
        savePost(
            "resolved-question",
            requester,
            privateProject,
            PostType.QUESTION,
            PostStatus.PUBLISHED);
    resolvedQuestion.resolve();
    postRepository.saveAndFlush(resolvedQuestion);

    savePost("draft-question", requester, privateProject, PostType.QUESTION, PostStatus.DRAFT);

    Post hiddenQuestion =
        savePost(
            "hidden-question", requester, privateProject, PostType.QUESTION, PostStatus.PUBLISHED);
    hiddenQuestion.hide(requester, "테스트 숨김");
    postRepository.saveAndFlush(hiddenQuestion);

    savePost("published-long", requester, privateProject, PostType.LONG, PostStatus.PUBLISHED);
    savePost(
        "other-project-question",
        outsider,
        unrelatedProject,
        PostType.QUESTION,
        PostStatus.PUBLISHED);

    List<MyProjectDto> projects = projectQueryRepository.getMyProjects(requester.getId());

    assertThat(projects)
        .extracting(MyProjectDto::handle)
        .containsExactlyInAnyOrder(privateProject.getHandle(), publicProject.getHandle());

    MyProjectDto privateSummary = getByHandle(projects, privateProject.getHandle());
    assertThat(privateSummary.role()).isEqualTo(Role.ADMIN);
    assertThat(privateSummary.isOwner()).isTrue();
    assertThat(privateSummary.memberCount()).isEqualTo(2);
    assertThat(privateSummary.unresolvedQuestionCount()).isEqualTo(1);

    MyProjectDto publicSummary = getByHandle(projects, publicProject.getHandle());
    assertThat(publicSummary.role()).isEqualTo(Role.MEMBER);
    assertThat(publicSummary.isOwner()).isFalse();
    assertThat(publicSummary.memberCount()).isEqualTo(2);
    assertThat(publicSummary.unresolvedQuestionCount()).isZero();
  }

  @Test
  @DisplayName("[findPublicProjectsByUserId] 공개 사용자 목록에서는 비공개 프로젝트를 제외한다")
  void findPublicProjectsByUserId_excludesPrivateProjects() {
    User user = saveUser("public-project-profile");
    Project publicProject = saveProject("public-profile-project", true);
    Project privateProject = saveProject("private-profile-project", false);

    teammateRepository.saveAndFlush(Teammate.owner(publicProject, user));
    teammateRepository.saveAndFlush(Teammate.owner(privateProject, user));

    assertThat(projectRepository.findPublicProjectsByUserId(user.getId()))
        .extracting(Project::getHandle)
        .containsExactly(publicProject.getHandle());
  }

  @Test
  @DisplayName("[findPublicByFollowerId] 공개 팔로우 목록에서는 비공개 프로젝트를 제외한다")
  void findPublicByFollowerId_excludesPrivateProjects() {
    User user = saveUser("public-project-follow");
    Project publicProject = saveProject("public-followed-project", true);
    Project privateProject = saveProject("private-followed-project", false);

    projectFollowRelationshipRepository.saveAndFlush(
        ProjectFollowRelationship.builder().follower(user).project(publicProject).build());
    projectFollowRelationshipRepository.saveAndFlush(
        ProjectFollowRelationship.builder().follower(user).project(privateProject).build());

    assertThat(projectFollowRelationshipRepository.findPublicByFollowerId(user.getId()))
        .extracting(relationship -> relationship.getProject().getHandle())
        .containsExactly(publicProject.getHandle());
  }

  private User saveUser(String key) {
    return userRepository.saveAndFlush(
        User.builder().email(key + "@example.com").fullName("사용자").build());
  }

  private Project saveProject(String handle, boolean isPublic) {
    return projectRepository.saveAndFlush(
        Project.builder().handle(handle).name(handle).isPublic(isPublic).build());
  }

  private Post savePost(
      String slug, User author, Project project, PostType type, PostStatus status) {
    Post post =
        Post.builder()
            .slug(slug)
            .author(author)
            .project(project)
            .title(type == PostType.SHORT ? null : "제목")
            .type(type)
            .status(status)
            .content("본문")
            .build();
    post.updateContent("본문", "본문", 0);

    return postRepository.saveAndFlush(post);
  }

  private MyProjectDto getByHandle(List<MyProjectDto> projects, String handle) {
    return projects.stream()
        .filter(project -> project.handle().equals(handle))
        .findFirst()
        .orElseThrow();
  }
}
