package com.skkil.sync.project.service;

import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.project.constants.ProjectConstants;
import com.skkil.sync.project.dto.request.AddTeammateRequest;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateTeammateRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.exception.TeammateNotFoundException;
import com.skkil.sync.project.mapper.ProjectMapper;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final UserDomainService userDomainService;

  private final ProjectRepository projectRepository;

  private final TeammateRepository teammateRepository;

  private final ProjectMapper projectMapper;

  private final MediaDomainService mediaDomainService;

  public ProjectService(
      UserDomainService userDomainService,
      ProjectRepository projectRepository,
      TeammateRepository teammateRepository,
      ProjectMapper projectMapper,
      MediaDomainService mediaDomainService) {
    this.userDomainService = userDomainService;
    this.projectRepository = projectRepository;
    this.teammateRepository = teammateRepository;
    this.projectMapper = projectMapper;
    this.mediaDomainService = mediaDomainService;
  }

  @Transactional
  public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {
    Project project = Project.builder().name(request.name()).handle(request.handle()).build();

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

    Map<Long, URL> profileImageUrls =
        mediaDomainService.generatePublicGetUrls(teammates, t -> t.getUser().getProfileImage());

    var teammatesDto =
        teammates.stream()
            .map(t -> projectMapper.toGetProjectResponseTeammate(t, profileImageUrls))
            .toList();

    Teammate requester =
        requesterId == null
            ? null
            : teammateRepository
                .findByProjectIdAndUserId(project.getId(), requesterId)
                .orElse(null);

    return GetProjectResponse.builder()
        .handle(handle)
        .name(project.getName())
        .description(project.getDescription())
        .isPublic(project.isPublic())
        .teammates(teammatesDto)
        .hasMoreTeammates(teammates.size() > ProjectConstants.INITIAL_TEAMMATE_LOAD_LIMIT)
        .role(requester != null ? requester.getRole() : null)
        .recentActivities(List.of())
        .build();
  }

  @Transactional(readOnly = true)
  public GetProjectTeammatesResponse getProjectTeammates(String handle) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    List<Teammate> teammates = teammateRepository.findByProjectId(project.getId());

    Map<Long, URL> profileImageUrls =
        mediaDomainService.generatePublicGetUrls(teammates, t -> t.getUser().getProfileImage());

    var teammatesDto =
        teammates.stream()
            .map(t -> projectMapper.toGetProjectTeammatesResponseTeammate(t, profileImageUrls))
            .toList();

    return new GetProjectTeammatesResponse(teammatesDto);
  }

  @Transactional(readOnly = true)
  public GetProjectHandleAvailabilityResponse isProjectHandleAvailable(String handle) {
    return new GetProjectHandleAvailabilityResponse(!projectRepository.existsByHandle(handle));
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse getProjectsByUser(String handle) {
    User user = userDomainService.getUserByHandle(handle);

    var projects =
        projectRepository.findMyProjects(user.getId()).stream()
            .map(projectMapper::toGetProjectsResponseProject)
            .toList();

    return new GetProjectsResponse(projects);
  }

  @Transactional(readOnly = true)
  public SearchProjectsResponse searchMyProjects(Long userId, String query) {
    var projects =
        projectRepository.searchMyProjects(userId, query).stream()
            .map(projectMapper::toSearchProjectsResponseProject)
            .toList();

    return new SearchProjectsResponse(projects);
  }

  @Transactional(readOnly = true)
  public SearchProjectsResponse searchProjects(String query) {
    var projects =
        projectRepository.searchProjects(query).stream()
            .map(projectMapper::toSearchProjectsResponseProject)
            .toList();

    return new SearchProjectsResponse(projects);
  }

  @Transactional
  public void addTeammate(Long userId, String handle, AddTeammateRequest request) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    if (!teammateRepository.existsByProjectIdAndUserIdAndIsOwnerTrue(project.getId(), userId)) {
      throw new AccessDeniedException("Only the project owner can add teammates");
    }

    User user = userDomainService.getUserByHandle(request.teammateHandle());
    Teammate teammate = Teammate.member(project, user);
    project.addTeammate(teammate);
  }

  @Transactional
  public void updateProject(Long userId, String handle, UpdateProjectRequest request) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    if (!teammateRepository.existsByProjectIdAndUserIdAndIsOwnerTrue(project.getId(), userId)) {
      throw new AccessDeniedException("Only the project owner can update the project");
    }

    project.update(request.name(), request.description());

    if (request.handle() != null) {
      project.updateHandle(request.handle());
    }
  }

  @Transactional
  public void deleteProject(Long userId, String handle) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    if (!teammateRepository.existsByProjectIdAndUserIdAndIsOwnerTrue(project.getId(), userId)) {
      throw new AccessDeniedException("Only the project owner can delete the project");
    }

    projectRepository.delete(project);
  }

  @Transactional
  public void removeTeammate(Long userId, String projectHandle, String teammateHandle) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    if (!teammateRepository.existsByProjectIdAndUserIdAndIsOwnerTrue(project.getId(), userId)) {
      throw new AccessDeniedException("Only the project owner can remove teammates");
    }

    if (!teammateRepository
        .findByProjectIdAndUserHandle(project.getId(), teammateHandle)
        .isPresent()) {
      throw new TeammateNotFoundException();
    }

    teammateRepository.deleteByProjectIdAndUserHandle(project.getId(), teammateHandle);
  }

  @Transactional
  public void updateTeammateRole(
      Long userId, String projectHandle, String teammateHandle, UpdateTeammateRequest request) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    if (!teammateRepository.existsByProjectIdAndUserIdAndIsOwnerTrue(project.getId(), userId)) {
      throw new AccessDeniedException("Only the project owner can update teammate roles");
    }

    Teammate teammate =
        teammateRepository
            .findByProjectIdAndUserHandle(project.getId(), teammateHandle)
            .orElseThrow(TeammateNotFoundException::new);

    teammate.setRole(request.role());
  }
}
