package com.skkil.sync.project.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.project.exception.ProjectOwnerCannotLeaveException;
import com.skkil.sync.project.exception.TeammateNotFoundException;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeammateServiceTests {

  @Mock private ProjectRepository projectRepository;

  @Mock private TeammateRepository teammateRepository;

  @Mock private UserDomainService userDomainService;

  @Mock private ProjectAssembler projectAssembler;

  private TeammateService teammateService;

  @BeforeEach
  void setUp() {
    teammateService =
        new TeammateService(
            projectRepository, teammateRepository, userDomainService, projectAssembler);
  }

  @Test
  @DisplayName("[leaveProject] 일반 멤버는 자신의 프로젝트 멤버십을 삭제할 수 있다")
  void leaveProject_member_deletesMembership() {
    Long userId = 1L;
    Project project = createProject();
    Teammate teammate = Teammate.member(project, new User(userId));

    when(projectRepository.findByHandle(project.getHandle())).thenReturn(Optional.of(project));
    when(teammateRepository.findByProjectIdAndUserId(project.getId(), userId))
        .thenReturn(Optional.of(teammate));

    teammateService.leaveProject(userId, project.getHandle());

    verify(teammateRepository).delete(teammate);
  }

  @Test
  @DisplayName("[leaveProject] 프로젝트 소유자가 나가려고 하면 전용 예외를 던진다")
  void leaveProject_projectOwner_throwsProjectOwnerCannotLeaveException() {
    Long userId = 1L;
    Project project = createProject();
    Teammate owner = Teammate.owner(project, new User(userId));

    when(projectRepository.findByHandle(project.getHandle())).thenReturn(Optional.of(project));
    when(teammateRepository.findByProjectIdAndUserId(project.getId(), userId))
        .thenReturn(Optional.of(owner));

    assertThatThrownBy(() -> teammateService.leaveProject(userId, project.getHandle()))
        .isInstanceOf(ProjectOwnerCannotLeaveException.class);
    verify(teammateRepository, never()).delete(owner);
  }

  @Test
  @DisplayName("[leaveProject] 프로젝트 멤버가 아니면 멤버 없음 예외를 던진다")
  void leaveProject_nonTeammate_throwsTeammateNotFoundException() {
    Long userId = 1L;
    Project project = createProject();

    when(projectRepository.findByHandle(project.getHandle())).thenReturn(Optional.of(project));
    when(teammateRepository.findByProjectIdAndUserId(project.getId(), userId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> teammateService.leaveProject(userId, project.getHandle()))
        .isInstanceOf(TeammateNotFoundException.class);
  }

  private static Project createProject() {
    Project project =
        Project.builder().handle("my-project").name("테스트 프로젝트").isPublic(true).build();
    project.setId(1L);
    return project;
  }
}
