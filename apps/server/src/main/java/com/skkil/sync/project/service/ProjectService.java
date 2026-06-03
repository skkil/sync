package com.skkil.sync.project.service;

import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final UserDomainService userDomainService;

  private final ProjectRepository projectRepository;

  public ProjectService(UserDomainService userDomainService, ProjectRepository projectRepository) {
    this.userDomainService = userDomainService;
    this.projectRepository = projectRepository;
  }

  @Transactional
  public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {
    Project project = Project.builder().name(request.name()).handle(request.handle()).build();

    User user = userDomainService.getUserReference(userId);
    Teammate owner = Teammate.builder().project(project).user(user).build();
    owner.setOwner(true);

    project.addTeammate(owner);

    project = projectRepository.save(project);

    return new CreateProjectResponse(project.getHandle());
  }

  @Transactional(readOnly = true)
  public GetProjectResponse getProjectByHandle(String handle) {
    Project project =
        projectRepository.findByHandle(handle).orElseThrow(ProjectNotFoundException::new);

    var teammates =
        project.getTeammates().stream()
            .map(t -> new GetProjectResponse.Teammate(t.getId(), t.getIsOwner()))
            .toList();

    return new GetProjectResponse(project.getHandle(), project.getName(), teammates);
  }

  @Transactional(readOnly = true)
  public GetProjectHandleAvailabilityResponse isProjectHandleAvailable(String handle) {
    return new GetProjectHandleAvailabilityResponse(!projectRepository.existsByHandle(handle));
  }

  @Transactional(readOnly = true)
  public SearchProjectsResponse searchMyProjects(Long userId, String query) {
    var projects =
        projectRepository.findByTeammatesUserIdAndNameContainingIgnoreCase(userId, query).stream()
            .map(project -> new SearchProjectsResponse.Project(project.getId(), project.getName()))
            .toList();

    return new SearchProjectsResponse(projects);
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
