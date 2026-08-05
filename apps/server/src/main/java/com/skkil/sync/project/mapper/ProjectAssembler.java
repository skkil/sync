package com.skkil.sync.project.mapper;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.project.dto.data.MyProjectDto;
import com.skkil.sync.project.dto.data.ProjectFollowerDto;
import com.skkil.sync.project.dto.response.GetMyProjectInvitationsResponse;
import com.skkil.sync.project.dto.response.GetMyProjectJoinRequestsResponse;
import com.skkil.sync.project.dto.response.GetMyProjectsResponse;
import com.skkil.sync.project.dto.response.GetProjectFollowersResponse;
import com.skkil.sync.project.dto.response.GetProjectInvitationsResponse;
import com.skkil.sync.project.dto.response.GetProjectJoinRequestsResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.summary.MyProjectSummary;
import com.skkil.sync.project.dto.summary.ProjectInvitationSummary;
import com.skkil.sync.project.dto.summary.ProjectSummary;
import com.skkil.sync.project.dto.summary.ProjectTeammateSummary;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.ProjectInvitation;
import com.skkil.sync.project.model.ProjectJoinRequest;
import com.skkil.sync.project.model.Role;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.user.dto.summary.UserSummary;
import com.skkil.sync.user.mapper.UserAssembler;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProjectAssembler {

  private final ProjectMapper projectMapper;

  private final UserAssembler userAssembler;

  private final MediaDomainService mediaDomainService;

  private final ProjectRepository projectRepository;

  public ProjectAssembler(
      ProjectMapper projectMapper,
      UserAssembler userAssembler,
      MediaDomainService mediaDomainService,
      ProjectRepository projectRepository) {
    this.projectMapper = projectMapper;
    this.userAssembler = userAssembler;
    this.mediaDomainService = mediaDomainService;
    this.projectRepository = projectRepository;
  }

  public Map<Long, ProjectSummary> toProjectSummaries(List<Long> projectIds) {
    List<Project> projects = projectRepository.findAllById(projectIds);
    Map<Long, URL> iconUrls =
        mediaDomainService.generatePresignedGetUrls(projects, Project::getIcon);

    return projects.stream()
        .collect(
            Collectors.toMap(
                Project::getId, project -> toProjectSummary(project, iconUrls), (a, b) -> a));
  }

  public GetProjectResponse toGetProjectResponse(
      Project project,
      List<Teammate> teammates,
      boolean hasMoreTeammates,
      Role requesterRole,
      boolean isOwner,
      boolean isFollowing,
      boolean hasPendingInvitation,
      boolean hasPendingJoinRequest) {
    return GetProjectResponse.builder()
        .summary(toProjectSummary(project))
        .teammates(toProjectTeammates(teammates))
        .hasMoreTeammates(hasMoreTeammates)
        .isViewer(requesterRole != null)
        .role(requesterRole)
        .isOwner(isOwner)
        .isFollowing(isFollowing)
        .hasPendingInvitation(hasPendingInvitation)
        .hasPendingJoinRequest(hasPendingJoinRequest)
        .build();
  }

  public GetProjectTeammatesResponse toGetProjectTeammatesResponse(List<Teammate> teammates) {
    return new GetProjectTeammatesResponse(toProjectTeammates(teammates));
  }

  public GetProjectsResponse toGetProjectsResponse(List<Project> projects) {
    Map<Long, URL> iconUrls =
        mediaDomainService.generatePresignedGetUrls(projects, Project::getIcon);

    return new GetProjectsResponse(
        projects.stream().map(project -> toProjectSummary(project, iconUrls)).toList());
  }

  public GetMyProjectsResponse toGetMyProjectsResponse(List<MyProjectDto> projects) {
    var iconMediaIds =
        projects.stream()
            .map(MyProjectDto::iconMediaId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    Map<Long, URL> iconUrls =
        iconMediaIds.isEmpty()
            ? Map.of()
            : mediaDomainService.generatePresignedGetUrlsByIds(iconMediaIds);

    return new GetMyProjectsResponse(
        projects.stream()
            .map(
                project -> {
                  URL iconUrl =
                      project.iconMediaId() == null ? null : iconUrls.get(project.iconMediaId());

                  return MyProjectSummary.builder()
                      .handle(project.handle())
                      .name(project.name())
                      .description(project.description())
                      .website(project.website())
                      .isPublic(project.isPublic())
                      .joinPolicy(project.joinPolicy())
                      .followerCount(project.followerCount())
                      .iconUrl(iconUrl == null ? null : iconUrl.toExternalForm())
                      .role(project.role())
                      .isOwner(project.isOwner())
                      .memberCount(project.memberCount())
                      .unresolvedQuestionCount(project.unresolvedQuestionCount())
                      .build();
                })
            .toList());
  }

  public GetProjectFollowersResponse toGetProjectFollowersResponse(
      CursorPaginationResponse<ProjectFollowerDto> followers) {
    return new GetProjectFollowersResponse(
        followers.mapWithLookup(
            ProjectFollowerDto::userId,
            userAssembler::toUserSummaries,
            (dto, summaries) ->
                new GetProjectFollowersResponse.Follower(summaries.get(dto.userId()))));
  }

  public GetProjectInvitationsResponse toGetProjectInvitationsResponse(
      List<ProjectInvitation> invitations) {
    Map<Long, UserSummary> inviteeSummaries =
        userAssembler.toUserSummaries(
            invitations.stream().map(invitation -> invitation.getInvitee().getId()).toList());
    Map<Long, UserSummary> inviterSummaries =
        userAssembler.toUserSummaries(
            invitations.stream().map(invitation -> invitation.getInviter().getId()).toList());

    var dtos =
        invitations.stream()
            .map(
                invitation ->
                    new GetProjectInvitationsResponse.Invitation(
                        toProjectInvitationSummary(invitation, inviterSummaries),
                        inviteeSummaries.get(invitation.getInvitee().getId())))
            .toList();

    return new GetProjectInvitationsResponse(dtos);
  }

  public GetMyProjectInvitationsResponse toGetMyProjectInvitationsResponse(
      List<ProjectInvitation> invitations) {
    Map<Long, UserSummary> inviterSummaries =
        userAssembler.toUserSummaries(
            invitations.stream().map(invitation -> invitation.getInviter().getId()).toList());

    var dtos =
        invitations.stream()
            .map(
                invitation ->
                    new GetMyProjectInvitationsResponse.Invitation(
                        toProjectInvitationSummary(invitation, inviterSummaries),
                        invitation.getToken(),
                        toProjectSummary(invitation.getProject())))
            .toList();

    return new GetMyProjectInvitationsResponse(dtos);
  }

  public GetProjectJoinRequestsResponse toGetProjectJoinRequestsResponse(
      List<ProjectJoinRequest> joinRequests) {
    Map<Long, UserSummary> requesterSummaries =
        userAssembler.toUserSummaries(
            joinRequests.stream().map(request -> request.getRequester().getId()).toList());

    var dtos =
        joinRequests.stream()
            .map(
                request ->
                    new GetProjectJoinRequestsResponse.JoinRequest(
                        request.getId(),
                        requesterSummaries.get(request.getRequester().getId()),
                        request.getCreatedAt()))
            .toList();

    return new GetProjectJoinRequestsResponse(dtos);
  }

  public GetMyProjectJoinRequestsResponse toGetMyProjectJoinRequestsResponse(
      List<ProjectJoinRequest> joinRequests) {
    var dtos =
        joinRequests.stream()
            .map(
                request ->
                    new GetMyProjectJoinRequestsResponse.JoinRequest(
                        request.getId(),
                        toProjectSummary(request.getProject()),
                        request.getCreatedAt()))
            .toList();

    return new GetMyProjectJoinRequestsResponse(dtos);
  }

  private ProjectSummary toProjectSummary(Project project) {
    String iconUrl =
        project.getIcon() != null
            ? mediaDomainService.generatePresignedGetUrl(project.getIcon()).toExternalForm()
            : null;

    return projectMapper.toProjectSummary(project, iconUrl);
  }

  private ProjectSummary toProjectSummary(Project project, Map<Long, URL> iconUrls) {
    var icon = project.getIcon();
    URL url = icon != null ? iconUrls.get(icon.getId()) : null;

    return projectMapper.toProjectSummary(project, url != null ? url.toExternalForm() : null);
  }

  private ProjectInvitationSummary toProjectInvitationSummary(
      ProjectInvitation invitation, Map<Long, UserSummary> inviterSummaries) {
    return ProjectInvitationSummary.builder()
        .id(invitation.getId())
        .inviter(inviterSummaries.get(invitation.getInviter().getId()))
        .role(invitation.getRole())
        .expiresAt(invitation.getExpiresAt())
        .build();
  }

  private List<ProjectTeammateSummary> toProjectTeammates(List<Teammate> teammates) {
    Map<Long, UserSummary> userSummaries =
        userAssembler.toUserSummaries(teammates.stream().map(t -> t.getUser().getId()).toList());

    return teammates.stream()
        .map(
            t ->
                ProjectTeammateSummary.builder()
                    .user(userSummaries.get(t.getUser().getId()))
                    .role(t.getRole())
                    .isOwner(t.isProjectOwner())
                    .build())
        .toList();
  }
}
