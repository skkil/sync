package com.skkil.sync.project.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Role;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectPermissionEvaluatorTests {

  @Mock private ProjectRepository projectRepository;

  @Mock private TeammateRepository teammateRepository;

  private ProjectPermissionEvaluator permissionEvaluator;

  @BeforeEach
  void setUp() {
    permissionEvaluator = new ProjectPermissionEvaluator(projectRepository, teammateRepository);
  }

  @Test
  @DisplayName("[hasPermission] 프로젝트 소유자는 프로젝트를 삭제할 수 있다")
  void hasPermission_delete_projectOwner_returnsTrue() {
    String projectHandle = "my-project";
    Long userId = 1L;
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId);
    Project project = createProject(projectHandle);
    Teammate owner = Teammate.owner(project, new User(userId));

    when(teammateRepository.findByProjectHandleAndUserId(projectHandle, userId))
        .thenReturn(Optional.of(owner));

    assertThat(
            permissionEvaluator.hasPermission(
                authenticatedUser, projectHandle, PermissionOperation.DELETE))
        .isTrue();
  }

  @Test
  @DisplayName("[hasPermission] 소유자가 아닌 관리자는 프로젝트를 수정할 수 있지만 삭제할 수 없다")
  void hasPermission_delete_nonOwnerAdmin_returnsFalse() {
    String projectHandle = "my-project";
    Long userId = 1L;
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId);
    Project project = createProject(projectHandle);
    Teammate admin = Teammate.member(project, new User(userId));
    admin.setRole(Role.ADMIN);

    when(teammateRepository.findByProjectHandleAndUserId(projectHandle, userId))
        .thenReturn(Optional.of(admin));

    assertThat(
            permissionEvaluator.hasPermission(
                authenticatedUser, projectHandle, PermissionOperation.EDIT))
        .isTrue();
    assertThat(
            permissionEvaluator.hasPermission(
                authenticatedUser, projectHandle, PermissionOperation.DELETE))
        .isFalse();
  }

  @Test
  @DisplayName("[hasPermission] 프로젝트 멤버가 아니면 프로젝트를 삭제할 수 없다")
  void hasPermission_delete_nonTeammate_returnsFalse() {
    String projectHandle = "my-project";
    Long userId = 1L;
    AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId);

    when(teammateRepository.findByProjectHandleAndUserId(projectHandle, userId))
        .thenReturn(Optional.empty());

    assertThat(
            permissionEvaluator.hasPermission(
                authenticatedUser, projectHandle, PermissionOperation.DELETE))
        .isFalse();
  }

  private static Project createProject(String handle) {
    return Project.builder().handle(handle).name("테스트 프로젝트").isPublic(true).build();
  }
}
