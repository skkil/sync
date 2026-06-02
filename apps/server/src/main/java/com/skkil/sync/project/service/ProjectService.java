package com.skkil.sync.project.service;

import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Transactional
  public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {
    Project project = Project.builder().name(request.name()).build();
    project = projectRepository.save(project);

    return new CreateProjectResponse(project.getId());
  }

  @Transactional(readOnly = true)
  public SearchProjectsResponse searchProjects(String query) {
    var projects =
        projectRepository.searchProjects(query).stream()
            .map(project -> new SearchProjectsResponse.Project(project.getId(), project.getName()))
            .toList();

    return new SearchProjectsResponse(projects);
  }
}
