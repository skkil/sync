package com.skkil.sync.common.seeder;

import com.skkil.sync.project.dto.request.AddTeammateRequest;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.service.ProjectService;
import com.skkil.sync.project.service.TeammateService;
import com.skkil.sync.user.model.User;
import org.springframework.stereotype.Component;

@Component
class ProjectSeeder {

  private final ProjectService projectService;
  private final TeammateService teammateService;

  ProjectSeeder(ProjectService projectService, TeammateService teammateService) {
    this.projectService = projectService;
    this.teammateService = teammateService;
  }

  String seed(User owner, String handle, String name, String description) {
    projectService.createProject(
        owner.getId(), new CreateProjectRequest(handle, name, description, true));
    return handle;
  }

  void addTeammate(User owner, String projectHandle, String teammateHandle) {
    SeedSecurityContext.runAs(
        owner,
        () -> teammateService.addTeammate(projectHandle, new AddTeammateRequest(teammateHandle)));
  }
}
