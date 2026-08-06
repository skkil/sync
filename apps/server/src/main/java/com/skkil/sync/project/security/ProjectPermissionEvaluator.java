package com.skkil.sync.project.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.project.exception.ProjectNotFoundException;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import org.springframework.stereotype.Component;

@Component
public class ProjectPermissionEvaluator implements CustomPermissionEvaluator<String> {

  private final ProjectRepository projectRepository;
  private final TeammateRepository teammateRepository;

  public ProjectPermissionEvaluator(
      ProjectRepository projectRepository, TeammateRepository teammateRepository) {
    this.projectRepository = projectRepository;
    this.teammateRepository = teammateRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.PROJECT;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, String projectHandle, PermissionOperation permission) {
    if (permission == PermissionOperation.READ) {
      var project =
          projectRepository.findByHandle(projectHandle).orElseThrow(ProjectNotFoundException::new);
      if (project.isPublic()) {
        return true;
      }
    }

    if (user == null) {
      return false;
    }

    return switch (permission) {
      case READ, CREATE ->
          teammateRepository.findByProjectHandleAndUserId(projectHandle, user.userId()).isPresent();
      case EDIT ->
          teammateRepository
              .findByProjectHandleAndUserId(projectHandle, user.userId())
              .map(Teammate::canManageProject)
              .orElse(false);
      case DELETE ->
          teammateRepository
              .findByProjectHandleAndUserId(projectHandle, user.userId())
              .map(Teammate::isProjectOwner)
              .orElse(false);
    };
  }
}
