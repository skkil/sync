package com.skkil.sync.provider.project.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.project.dto.request.CreateTeamBuildingPostRequest;
import com.skkil.sync.provider.project.dto.response.CreateTeamBuildingPostResponse;
import com.skkil.sync.provider.project.dto.response.GetTeamBuildingPostsResponse;
import com.skkil.sync.provider.project.mapper.ProjectMapper;
import com.skkil.sync.provider.project.model.Project;
import com.skkil.sync.provider.project.model.TeamBuildingPost;
import com.skkil.sync.provider.project.repository.ProjectRepository;
import com.skkil.sync.provider.project.repository.TeamBuildingPostQueryRepository;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import com.skkil.sync.provider.project.repository.pagination.TeamBuildingPostCursorPaginationProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamBuildingService {

  private final ProjectRepository projectRepository;
  private final TeamBuildingPostRepository teamBuildingPostRepository;
  private final TeamBuildingPostQueryRepository teamBuildingPostQueryRepository;
  private final PaginationService paginationService;
  private final TeamBuildingPostCursorPaginationProvider paginationProvider;
  private final ProjectMapper projectMapper;

  public TeamBuildingService(
      ProjectRepository projectRepository,
      TeamBuildingPostRepository teamBuildingPostRepository,
      TeamBuildingPostQueryRepository teamBuildingPostQueryRepository,
      PaginationService paginationService,
      TeamBuildingPostCursorPaginationProvider paginationProvider,
      ProjectMapper projectMapper) {
    this.projectRepository = projectRepository;
    this.teamBuildingPostRepository = teamBuildingPostRepository;
    this.teamBuildingPostQueryRepository = teamBuildingPostQueryRepository;
    this.paginationService = paginationService;
    this.paginationProvider = paginationProvider;
    this.projectMapper = projectMapper;
  }

  @Transactional
  @PreAuthorize("hasPermission(#projectId, 'PROVIDER', 'EDIT')")
  public CreateTeamBuildingPostResponse createTeamBuildingPost(
      Long projectId, CreateTeamBuildingPostRequest request) {
    Project project = projectRepository.getReferenceById(projectId);

    TeamBuildingPost post =
        TeamBuildingPost.builder()
            .project(project)
            .title(request.title())
            .content(request.content())
            .build();
    post = teamBuildingPostRepository.save(post);

    return new CreateTeamBuildingPostResponse(post.getId().toString());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#projectId, 'PROVIDER', 'READ')")
  public GetTeamBuildingPostsResponse getTeamBuildingPostsByProject(
      Long projectId, CursorPaginationRequest pagination) {
    var posts =
        paginationService
            .paginate(
                teamBuildingPostQueryRepository.getTeamBuildingPostsByProject(projectId),
                paginationProvider,
                pagination)
            .map(projectMapper::toPostResponse);

    return new GetTeamBuildingPostsResponse(posts);
  }

  @Transactional(readOnly = true)
  public GetTeamBuildingPostsResponse getTeamBuildingPosts(CursorPaginationRequest pagination) {
    var posts =
        paginationService
            .paginate(
                teamBuildingPostQueryRepository.getTeamBuildingPosts(),
                paginationProvider,
                pagination)
            .map(projectMapper::toPostResponse);

    return new GetTeamBuildingPostsResponse(posts);
  }
}
