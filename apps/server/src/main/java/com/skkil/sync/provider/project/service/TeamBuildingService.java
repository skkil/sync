package com.skkil.sync.provider.project.service;

import com.skkil.sync.provider.project.dto.request.CreateTeamBuildingPostRequest;
import com.skkil.sync.provider.project.dto.response.CreateTeamBuildingPostResponse;
import com.skkil.sync.provider.project.dto.response.GetProjectTeamBuildingPosts;
import com.skkil.sync.provider.project.model.Project;
import com.skkil.sync.provider.project.model.TeamBuildingPost;
import com.skkil.sync.provider.project.repository.ProjectRepository;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamBuildingService {

  private final ProjectRepository projectRepository;
  private final TeamBuildingPostRepository teamBuildingPostRepository;

  public TeamBuildingService(
      ProjectRepository projectRepository, TeamBuildingPostRepository teamBuildingPostRepository) {
    this.projectRepository = projectRepository;
    this.teamBuildingPostRepository = teamBuildingPostRepository;
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
  public GetProjectTeamBuildingPosts getProjectTeamBuildingPosts(Long projectId) {
    Project project = projectRepository.getReferenceById(projectId);

    var posts = teamBuildingPostRepository.findByProject(project);

    return new GetProjectTeamBuildingPosts(
        posts.stream()
            .map(
                post ->
                    GetProjectTeamBuildingPosts.Post.builder()
                        .id(post.getId().toString())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .build())
            .toList());
  }
}
