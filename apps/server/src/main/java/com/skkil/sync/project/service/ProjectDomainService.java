package com.skkil.sync.project.service;

import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectDomainService {

  private final ProjectRepository projectRepository;

  public ProjectDomainService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Transactional
  public Project getProject(Long projectId) {
    return projectRepository.findById(projectId).orElseThrow();
  }
}
