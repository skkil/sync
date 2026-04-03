package com.skkil.sync.provider.project.service;

import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.provider.project.dto.data.TeamBuildingPostDto;
import com.skkil.sync.provider.project.dto.query.TeamBuildingPostQuery;
import com.skkil.sync.provider.project.dto.request.CreateTeamBuildingPostRequest;
import com.skkil.sync.provider.project.dto.response.CreateTeamBuildingPostResponse;
import com.skkil.sync.provider.project.dto.response.GetTeamBuildingPostsResponse;
import com.skkil.sync.provider.project.model.Project;
import com.skkil.sync.provider.project.model.TeamBuildingPost;
import com.skkil.sync.provider.project.repository.ProjectRepository;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import com.skkil.sync.provider.project.repository.query.TeamBuildingPostQueryRepository;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamBuildingService {

  private final ProjectRepository projectRepository;
  private final TeamBuildingPostRepository teamBuildingPostRepository;
  private final TeamBuildingPostQueryRepository teamBuildingPostQueryRepository;
  private final CursorConverter cursorConverter;

  public TeamBuildingService(
      ProjectRepository projectRepository,
      TeamBuildingPostRepository teamBuildingPostRepository,
      TeamBuildingPostQueryRepository teamBuildingPostQueryRepository,
      CursorConverter cursorConverter) {
    this.projectRepository = projectRepository;
    this.teamBuildingPostRepository = teamBuildingPostRepository;
    this.teamBuildingPostQueryRepository = teamBuildingPostQueryRepository;
    this.cursorConverter = cursorConverter;
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
    Cursor cursor = cursorConverter.decode(pagination.cursor());
    TeamBuildingPostQuery query =
        TeamBuildingPostQuery.builder()
            .createdAfter(cursor != null ? cursor.createdAt() : null)
            .afterId(cursor != null ? cursor.id() : null)
            .projectId(projectId)
            .build();

    List<TeamBuildingPostDto> posts =
        teamBuildingPostQueryRepository.getTeamBuildingPosts(query, pagination.size() + 1);

    return mapToGetTeamBuildingPostsResponse(posts, pagination.size());
  }

  @Transactional(readOnly = true)
  public GetTeamBuildingPostsResponse getTeamBuildingPosts(CursorPaginationRequest pagination) {
    Cursor cursor = cursorConverter.decode(pagination.cursor());
    TeamBuildingPostQuery query =
        TeamBuildingPostQuery.builder()
            .createdAfter(cursor != null ? cursor.createdAt() : null)
            .afterId(cursor != null ? cursor.id() : null)
            .build();

    List<TeamBuildingPostDto> posts =
        teamBuildingPostQueryRepository.getTeamBuildingPosts(query, pagination.size() + 1);

    return mapToGetTeamBuildingPostsResponse(posts, pagination.size());
  }

  private GetTeamBuildingPostsResponse mapToGetTeamBuildingPostsResponse(
      List<TeamBuildingPostDto> posts, int size) {
    boolean hasNext = posts.size() > size;
    Cursor nextCursor = getNextCursor(posts.subList(0, Math.min(posts.size(), size)));

    List<GetTeamBuildingPostsResponse.Post> content =
        posts.stream()
            .limit(size)
            .map(
                post ->
                    new GetTeamBuildingPostsResponse.Post(
                        post.id().toString(),
                        new GetTeamBuildingPostsResponse.Project(
                            post.projectId().toString(),
                            post.projectName(),
                            post.projectDescription()),
                        post.title(),
                        post.content()))
            .toList();

    return new GetTeamBuildingPostsResponse(
        new CursorPaginationResponse<>(content, hasNext, cursorConverter.encode(nextCursor)));
  }

  private Cursor getNextCursor(List<TeamBuildingPostDto> posts) {
    if (posts.isEmpty()) {
      return null;
    }

    TeamBuildingPostDto lastPost = posts.get(posts.size() - 1);
    return Cursor.builder()
        .score(0L)
        .createdAt(lastPost.createdAt().toInstant(ZoneOffset.UTC))
        .updatedAt(lastPost.updatedAt().toInstant(ZoneOffset.UTC))
        .id(lastPost.id())
        .build();
  }
}
