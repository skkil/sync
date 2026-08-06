package com.skkil.sync.project.service;

import com.skkil.sync.project.dto.request.AddTeammateRequest;
import com.skkil.sync.project.dto.request.UpdateTeammateRequest;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.exception.ProjectOwnerCannotBeModifiedException;
import com.skkil.sync.project.exception.ProjectOwnerCannotLeaveException;
import com.skkil.sync.project.exception.TeammateNotFoundException;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeammateService {

  private final ProjectRepository projectRepository;

  private final TeammateRepository teammateRepository;

  private final UserDomainService userDomainService;

  private final ProjectAssembler projectAssembler;

  public TeammateService(
      ProjectRepository projectRepository,
      TeammateRepository teammateRepository,
      UserDomainService userDomainService,
      ProjectAssembler projectAssembler) {
    this.projectRepository = projectRepository;
    this.teammateRepository = teammateRepository;
    this.userDomainService = userDomainService;
    this.projectAssembler = projectAssembler;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'READ')")
  public GetProjectTeammatesResponse getProjectTeammates(String handle) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    List<Teammate> teammates = teammateRepository.findByProjectId(project.getId());

    return projectAssembler.toGetProjectTeammatesResponse(teammates);
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void addTeammate(String handle, AddTeammateRequest request) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    User user = userDomainService.getUserByHandle(request.teammateHandle());
    Teammate teammate = Teammate.member(project, user);
    project.addTeammate(teammate);
  }

  @Transactional
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'EDIT')")
  public void removeTeammate(String projectHandle, String teammateHandle) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    Teammate teammate =
        teammateRepository
            .findByProjectIdAndUserHandle(project.getId(), teammateHandle)
            .orElseThrow(TeammateNotFoundException::new);

    if (teammate.isProjectOwner()) {
      throw new ProjectOwnerCannotBeModifiedException();
    }

    teammateRepository.deleteByProjectIdAndUserHandle(project.getId(), teammateHandle);
  }

  @Transactional
  @PreAuthorize("#userId == principal.userId")
  public void leaveProject(Long userId, String projectHandle) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    Teammate teammate =
        teammateRepository
            .findByProjectIdAndUserId(project.getId(), userId)
            .orElseThrow(TeammateNotFoundException::new);

    if (teammate.isProjectOwner()) {
      throw new ProjectOwnerCannotLeaveException();
    }

    teammateRepository.delete(teammate);
  }

  @Transactional
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'EDIT')")
  public void updateTeammate(
      String projectHandle, String teammateHandle, UpdateTeammateRequest request) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    Teammate teammate =
        teammateRepository
            .findByProjectIdAndUserHandle(project.getId(), teammateHandle)
            .orElseThrow(TeammateNotFoundException::new);

    if (teammate.isProjectOwner()) {
      throw new ProjectOwnerCannotBeModifiedException();
    }

    teammate.setRole(request.role());
  }
}
