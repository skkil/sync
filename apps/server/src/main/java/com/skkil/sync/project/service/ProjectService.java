package com.skkil.sync.project.service;

import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.project.constants.ProjectConstants;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetMyProjectsResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.exception.ProjectHandleAlreadyExistsException;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.InvitationStatus;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectFollowRelationshipRepository;
import com.skkil.sync.project.repository.ProjectInvitationRepository;
import com.skkil.sync.project.repository.ProjectJoinRequestRepository;
import com.skkil.sync.project.repository.ProjectQueryRepository;
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

  private final ProjectQueryRepository projectQueryRepository;

  private final TeammateRepository teammateRepository;

  private final ProjectAssembler projectAssembler;

  private final MediaDomainService mediaDomainService;

  private final ProjectFollowRelationshipRepository projectFollowRelationshipRepository;

  private final ProjectInvitationRepository projectInvitationRepository;

  private final ProjectJoinRequestRepository projectJoinRequestRepository;

  public ProjectService(
      UserDomainService userDomainService,
      ProjectRepository projectRepository,
      ProjectQueryRepository projectQueryRepository,
      TeammateRepository teammateRepository,
      ProjectAssembler projectAssembler,
      MediaDomainService mediaDomainService,
      ProjectFollowRelationshipRepository projectFollowRelationshipRepository,
      ProjectInvitationRepository projectInvitationRepository,
      ProjectJoinRequestRepository projectJoinRequestRepository) {
    this.userDomainService = userDomainService;
    this.projectRepository = projectRepository;
    this.projectQueryRepository = projectQueryRepository;
    this.teammateRepository = teammateRepository;
    this.projectAssembler = projectAssembler;
    this.mediaDomainService = mediaDomainService;
    this.projectFollowRelationshipRepository = projectFollowRelationshipRepository;
    this.projectInvitationRepository = projectInvitationRepository;
    this.projectJoinRequestRepository = projectJoinRequestRepository;
  }

  @Transactional
  public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {
    Project project =
        Project.builder()
            .name(request.name())
            .handle(request.handle())
            .description(request.description())
            .isPublic(request.isPublic())
            .joinPolicy(request.joinPolicy())
            .build();

    User user = userDomainService.getUserReference(userId);
    Teammate owner = Teammate.owner(project, user);
    project.addTeammate(owner);

    project = projectRepository.save(project);

    return new CreateProjectResponse(project.getHandle());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'READ')")
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

    boolean isFollowing =
        requesterId != null
            && projectFollowRelationshipRepository.existsByFollowerAndProject(
                requesterId, project.getId());

    boolean hasPendingInvitation =
        requesterId != null
            && projectInvitationRepository.existsByProjectIdAndInviteeIdAndStatus(
                project.getId(), requesterId, InvitationStatus.PENDING);

    boolean hasPendingJoinRequest =
        requesterId != null
            && projectJoinRequestRepository.existsByProjectIdAndRequesterId(
                project.getId(), requesterId);

    return projectAssembler.toGetProjectResponse(
        project,
        teammates,
        teammates.size() > ProjectConstants.INITIAL_TEAMMATE_LOAD_LIMIT,
        requester != null ? requester.getRole() : null,
        requester != null && requester.isProjectOwner(),
        isFollowing,
        hasPendingInvitation,
        hasPendingJoinRequest);
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

    return projectAssembler.toGetProjectsResponse(
        projectRepository.findPublicProjectsByUserId(user.getId()));
  }

  @Transactional(readOnly = true)
  public GetMyProjectsResponse getMyProjects(Long userId) {
    return projectAssembler.toGetMyProjectsResponse(projectQueryRepository.getMyProjects(userId));
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

    project.update(request.name(), request.description(), request.website());
    project.updateJoinPolicy(request.joinPolicy());

    if (request.handle() != null) {
      String trimmedHandle = request.handle().trim();

      if (!trimmedHandle.equals(project.getHandle())) {
        if (projectRepository.existsByHandle(trimmedHandle)) {
          throw new ProjectHandleAlreadyExistsException();
        }

        project.updateHandle(trimmedHandle);
      }
    }

    if (Boolean.TRUE.equals(request.removeIcon())) {
      project.removeIcon();
    }

    if (request.iconMediaId() != null) {
      Media icon = mediaDomainService.linkMedia(requesterId, Long.valueOf(request.iconMediaId()));
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
