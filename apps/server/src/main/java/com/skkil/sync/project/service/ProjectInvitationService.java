package com.skkil.sync.project.service;

import com.skkil.sync.project.dto.request.CreateProjectInvitationRequest;
import com.skkil.sync.project.dto.response.GetMyProjectInvitationsResponse;
import com.skkil.sync.project.dto.response.GetProjectInvitationsResponse;
import com.skkil.sync.project.exception.ProjectInvitationAlreadyExistsException;
import com.skkil.sync.project.exception.ProjectInvitationExpiredException;
import com.skkil.sync.project.exception.ProjectInvitationNotFoundException;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.InvitationStatus;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.ProjectInvitation;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectInvitationRepository;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectInvitationService {

  private final ProjectRepository projectRepository;

  private final TeammateRepository teammateRepository;

  private final ProjectInvitationRepository projectInvitationRepository;

  private final UserDomainService userDomainService;

  private final ProjectAssembler projectAssembler;

  public ProjectInvitationService(
      ProjectRepository projectRepository,
      TeammateRepository teammateRepository,
      ProjectInvitationRepository projectInvitationRepository,
      UserDomainService userDomainService,
      ProjectAssembler projectAssembler) {
    this.projectRepository = projectRepository;
    this.teammateRepository = teammateRepository;
    this.projectInvitationRepository = projectInvitationRepository;
    this.userDomainService = userDomainService;
    this.projectAssembler = projectAssembler;
  }

  @Transactional
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'EDIT')")
  public void createInvitation(
      Long userId, String projectHandle, CreateProjectInvitationRequest request) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    User invitee = userDomainService.getUserByHandle(request.inviteeHandle());
    User inviter = userDomainService.getUserReference(userId);

    if (teammateRepository.findByProjectIdAndUserId(project.getId(), invitee.getId()).isPresent()
        || projectInvitationRepository.existsByProjectIdAndInviteeIdAndStatus(
            project.getId(), invitee.getId(), InvitationStatus.PENDING)) {
      throw new ProjectInvitationAlreadyExistsException();
    }

    ProjectInvitation invitation =
        ProjectInvitation.builder()
            .project(project)
            .inviter(inviter)
            .invitee(invitee)
            .role(request.role())
            .build();
    projectInvitationRepository.save(invitation);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'EDIT')")
  public GetProjectInvitationsResponse getProjectInvitations(String projectHandle) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    var invitations =
        projectInvitationRepository.findByProjectIdAndStatus(
            project.getId(), InvitationStatus.PENDING);

    return projectAssembler.toGetProjectInvitationsResponse(invitations);
  }

  @Transactional
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'EDIT')")
  public void cancelInvitation(String projectHandle, Long invitationId) {
    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    ProjectInvitation invitation =
        projectInvitationRepository
            .findById(invitationId)
            .filter(i -> i.getProject().getId().equals(project.getId()))
            .orElseThrow(ProjectInvitationNotFoundException::new);

    projectInvitationRepository.delete(invitation);
  }

  @Transactional(readOnly = true)
  public GetMyProjectInvitationsResponse getMyInvitations(Long userId) {
    var invitations =
        projectInvitationRepository
            .findByInviteeIdAndStatus(userId, InvitationStatus.PENDING)
            .stream()
            .filter(i -> !i.isExpired())
            .toList();

    return projectAssembler.toGetMyProjectInvitationsResponse(invitations);
  }

  @Transactional
  public void acceptInvitation(Long userId, String token) {
    User user = userDomainService.getUserReference(userId);
    ProjectInvitation invitation = getInvitationForUser(user, token);

    if (teammateRepository
        .findByProjectIdAndUserId(invitation.getProject().getId(), user.getId())
        .isPresent()) {
      invitation.accept();
      return;
    }

    Teammate teammate = Teammate.member(invitation.getProject(), user);
    teammate.setRole(invitation.getRole());
    invitation.getProject().addTeammate(teammate);

    invitation.accept();
  }

  @Transactional
  public void declineInvitation(Long userId, String token) {
    User user = userDomainService.getUserReference(userId);
    ProjectInvitation invitation = getInvitationForUser(user, token);

    invitation.decline();
  }

  private ProjectInvitation getInvitationForUser(User user, String token) {
    ProjectInvitation invitation =
        projectInvitationRepository
            .findByToken(token)
            .orElseThrow(ProjectInvitationNotFoundException::new);

    if (!invitation.getInvitee().getId().equals(user.getId())) {
      throw new ProjectInvitationNotFoundException();
    }

    if (!invitation.isPending()) {
      throw new ProjectInvitationNotFoundException();
    }

    if (invitation.isExpired()) {
      invitation.expire();
      throw new ProjectInvitationExpiredException();
    }

    return invitation;
  }
}
