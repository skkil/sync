package com.skkil.sync.project.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.project.dto.response.GetProjectFollowersResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.ProjectFollowRelationship;
import com.skkil.sync.project.repository.ProjectFollowRelationshipRepository;
import com.skkil.sync.project.repository.ProjectFollowerQueryRepository;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.pagination.ProjectFollowerCursorPaginationProvider;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.domain.UserDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProjectFollowService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final UserDomainService userDomainService;
  private final ProjectFollowRelationshipRepository projectFollowRelationshipRepository;
  private final ProjectFollowerQueryRepository projectFollowerQueryRepository;
  private final ProjectFollowerCursorPaginationProvider followerPaginationProvider;
  private final ProjectAssembler projectAssembler;
  private final PaginationService paginationService;

  public ProjectFollowService(
      ProjectRepository projectRepository,
      UserRepository userRepository,
      UserDomainService userDomainService,
      ProjectFollowRelationshipRepository projectFollowRelationshipRepository,
      ProjectFollowerQueryRepository projectFollowerQueryRepository,
      ProjectFollowerCursorPaginationProvider followerPaginationProvider,
      ProjectAssembler projectAssembler,
      PaginationService paginationService) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.userDomainService = userDomainService;
    this.projectFollowRelationshipRepository = projectFollowRelationshipRepository;
    this.projectFollowerQueryRepository = projectFollowerQueryRepository;
    this.followerPaginationProvider = followerPaginationProvider;
    this.projectAssembler = projectAssembler;
    this.paginationService = paginationService;
  }

  @Transactional
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'READ')")
  public void followProject(Long followerId, String projectHandle) {
    log.debug("User {} is attempting to follow project {}", followerId, projectHandle);

    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    if (projectFollowRelationshipRepository.existsByFollowerAndProject(
        followerId, project.getId())) {
      log.debug("User {} is already following project {}", followerId, projectHandle);
      return;
    }

    User follower = userRepository.getReferenceById(followerId);
    var relationship =
        ProjectFollowRelationship.builder().follower(follower).project(project).build();

    try {
      // saveAndFlush is required here (rather than save) so that a unique-constraint
      // violation from a concurrent follow request surfaces inside this try block
      // instead of at transaction commit, after the method has already returned.
      projectFollowRelationshipRepository.saveAndFlush(relationship);
    } catch (DataIntegrityViolationException e) {
      log.debug(
          "User {} was concurrently followed to project {}, ignoring duplicate",
          followerId,
          projectHandle);
      return;
    }

    projectRepository.incrementFollowerCount(project.getId());
  }

  @Transactional
  public void unfollowProject(Long followerId, String projectHandle) {
    log.debug("User {} is attempting to unfollow project {}", followerId, projectHandle);

    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    int deleted =
        projectFollowRelationshipRepository.deleteByFollowerAndProject(followerId, project.getId());
    if (deleted == 0) {
      return;
    }

    projectRepository.decrementFollowerCount(project.getId());
  }

  @Transactional(readOnly = true)
  public boolean isFollowing(Long followerId, String projectHandle) {
    log.debug("Checking if user {} is following project {}", followerId, projectHandle);

    if (followerId == null || projectHandle == null) {
      return false;
    }

    Project project = projectRepository.findByHandle(projectHandle).orElse(null);
    if (project == null) {
      return false;
    }

    return projectFollowRelationshipRepository.existsByFollowerAndProject(
        followerId, project.getId());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#projectHandle, 'PROJECT', 'READ')")
  public GetProjectFollowersResponse getFollowers(
      Long requesterId, String projectHandle, CursorPaginationRequest pagination) {
    log.debug("Retrieving followers of project {}", projectHandle);

    Project project =
        projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);

    var followers =
        paginationService.paginate(
            projectFollowerQueryRepository.getFollowers(project.getId()),
            followerPaginationProvider,
            pagination);

    return projectAssembler.toGetProjectFollowersResponse(followers);
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse getFollowedProjects(String userHandle) {
    log.debug("Retrieving projects followed by user {}", userHandle);

    User user = userDomainService.getUserByHandle(userHandle);

    var projects =
        projectFollowRelationshipRepository.findByFollowerId(user.getId()).stream()
            .map(ProjectFollowRelationship::getProject)
            .toList();

    return projectAssembler.toGetProjectsResponse(projects);
  }
}
