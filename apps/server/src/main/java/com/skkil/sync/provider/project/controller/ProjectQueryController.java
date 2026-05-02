package com.skkil.sync.provider.project.controller;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.provider.project.dto.response.GetProjectsResponse;
import com.skkil.sync.provider.project.service.ProjectQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectQueryController {

  private final ProjectQueryService projectQueryService;

  public ProjectQueryController(ProjectQueryService projectQueryService) {
    this.projectQueryService = projectQueryService;
  }

  @GetMapping("/projects/trending")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectsResponse getTrendingProjects(@Validated CursorPaginationRequest pagination) {
    return projectQueryService.getTrendingProjects(pagination);
  }
}
