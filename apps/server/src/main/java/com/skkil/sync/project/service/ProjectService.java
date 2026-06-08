package com.skkil.sync.project.service;

import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.mapper.ProjectMapper;
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

  private final ProjectMapper projectMapper;

  public ProjectService(
      UserDomainService userDomainService,
      ProjectRepository projectRepository,
      ProjectMapper projectMapper) {
    this.userDomainService = userDomainService;
    this.projectRepository = projectRepository;
    this.projectMapper = projectMapper;
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
            .map(t -> new GetProjectResponse.Teammate(t.getUser().getId(), t.getIsOwner()))
            .toList();

    return new GetProjectResponse(project.getHandle(), project.getName(), teammates);
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
}
