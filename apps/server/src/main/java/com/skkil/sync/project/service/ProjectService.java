package com.skkil.sync.project.service;

import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.project.constants.ProjectConstants;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final UserDomainService userDomainService;

  private final ProjectRepository projectRepository;

  private final TeammateRepository teammateRepository;

  private final ProjectAssembler projectAssembler;

  private final MediaDomainService mediaDomainService;

  public ProjectService(
      UserDomainService userDomainService,
      ProjectRepository projectRepository,
      TeammateRepository teammateRepository,
      ProjectAssembler projectAssembler,
      MediaDomainService mediaDomainService) {
    this.userDomainService = userDomainService;
    this.projectRepository = projectRepository;
    this.teammateRepository = teammateRepository;
    this.projectAssembler = projectAssembler;
    this.mediaDomainService = mediaDomainService;
  }

  @Transactional
  public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {
    Project project =
        Project.builder()
            .name(request.name())
            .handle(request.handle())
            .description(request.description())
            .isPublic(request.isPublic())
            .build();

    User user = userDomainService.getUserReference(userId);
    Teammate owner = Teammate.owner(project, user);
    project.addTeammate(owner);

    project = projectRepository.save(project);

    return new CreateProjectResponse(project.getHandle());
  }

  @Transactional(readOnly = true)
  public GetProjectResponse getProjectByHandle(Long requesterId, String handle) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    List<Teammate> teammates =
        teammateRepository.findByProjectId(
            project.getId(), PageRequest.of(0, ProjectConstants.INITIAL_TEAMMATE_LOAD_LIMIT + 1));

    Teammate requester =
        requesterId == null
            ? null
            : teammateRepository
                .findByProjectIdAndUserId(project.getId(), requesterId)
                .orElse(null);

    return projectAssembler.toGetProjectResponse(
        project,
        teammates,
        teammates.size() > ProjectConstants.INITIAL_TEAMMATE_LOAD_LIMIT,
        requester != null ? requester.getRole() : null);
  }

  @Transactional(readOnly = true)
  public GetProjectHandleAvailabilityResponse isProjectHandleAvailable(String handle) {
    boolean isReserved =
        ProjectConstants.RESERVED_HANDLES.contains(handle.toLowerCase(Locale.ROOT));

    return new GetProjectHandleAvailabilityResponse(
        !isReserved && !projectRepository.existsByHandle(handle));
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse getProjectsByUser(String handle) {
    User user = userDomainService.getUserByHandle(handle);

    return projectAssembler.toGetProjectsResponse(projectRepository.findMyProjects(user.getId()));
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse searchMyProjects(Long userId, String query) {
    return projectAssembler.toGetProjectsResponse(
        projectRepository.searchMyProjects(userId, query));
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse searchProjects(String query) {
    return projectAssembler.toGetProjectsResponse(projectRepository.searchProjects(query));
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void updateProject(Long requesterId, String handle, UpdateProjectRequest request) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    project.update(request.description(), request.website());

    if (Boolean.TRUE.equals(request.removeIcon())) {
      project.removeIcon();
    }

    if (request.iconMediaId() != null) {
      Media icon =
          mediaDomainService.getUnlinkedMedia(requesterId, Long.valueOf(request.iconMediaId()));
      project.setIcon(icon);
    }
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'DELETE')")
  public void deleteProject(String handle) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    projectRepository.delete(project);
  }
}
