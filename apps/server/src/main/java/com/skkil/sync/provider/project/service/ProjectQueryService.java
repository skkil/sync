package com.skkil.sync.provider.project.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.project.dto.response.GetProjectsResponse;
import com.skkil.sync.provider.project.mapper.ProjectMapper;
import com.skkil.sync.provider.project.repository.ProjectQueryRepository;
import com.skkil.sync.provider.project.repository.pagination.ProjectCursorPaginationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectQueryService {

  private final ProjectQueryRepository projectQueryRepository;
  private final PaginationService paginationService;
  private final ProjectCursorPaginationProvider paginationProvider;
  private final ProjectMapper projectMapper;

  public ProjectQueryService(
      ProjectQueryRepository projectQueryRepository,
      PaginationService paginationService,
      ProjectCursorPaginationProvider paginationProvider,
      ProjectMapper projectMapper) {
    this.projectQueryRepository = projectQueryRepository;
    this.paginationService = paginationService;
    this.paginationProvider = paginationProvider;
    this.projectMapper = projectMapper;
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse getTrendingProjects(CursorPaginationRequest pagination) {
    var projects =
        paginationService
            .paginate(projectQueryRepository.getTrendingProjects(), paginationProvider, pagination)
            .map(projectMapper::toProjectResponse);

    return new GetProjectsResponse(projects);
  }
}
