package com.skkil.sync.provider.project.service.query;

import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.provider.project.dto.data.ProjectDto;
import com.skkil.sync.provider.project.dto.response.GetProjectsResponse;
import com.skkil.sync.provider.project.repository.query.ProjectQueryRepository;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectQueryService {

  private final ProjectQueryRepository projectQueryRepository;
  private final CursorConverter cursorConverter;

  public ProjectQueryService(
      ProjectQueryRepository projectQueryRepository, CursorConverter cursorConverter) {
    this.projectQueryRepository = projectQueryRepository;
    this.cursorConverter = cursorConverter;
  }

  @Transactional(readOnly = true)
  public GetProjectsResponse getTrendingProjects(CursorPaginationRequest pagination) {
    Cursor cursor = cursorConverter.decode(pagination.cursor());

    List<ProjectDto> projects =
        projectQueryRepository.getTrendingProjects(
            cursor != null ? cursor.createdAt() : null,
            cursor != null ? cursor.id() : null,
            pagination.size() + 1);

    return mapToGetProjectsResponse(projects, pagination.size());
  }

  private GetProjectsResponse mapToGetProjectsResponse(List<ProjectDto> projects, int size) {
    boolean hasNext = projects.size() > size;
    Cursor nextCursor = getNextCursor(projects.subList(0, Math.min(projects.size(), size)));

    List<GetProjectsResponse.Project> content =
        projects.stream()
            .limit(size)
            .map(
                project ->
                    new GetProjectsResponse.Project(
                        project.id(), project.name(), project.description()))
            .toList();

    return new GetProjectsResponse(
        new CursorPaginationResponse<>(content, hasNext, cursorConverter.encode(nextCursor)));
  }

  private Cursor getNextCursor(List<ProjectDto> projects) {
    if (projects.isEmpty()) {
      return null;
    }

    ProjectDto lastProject = projects.get(projects.size() - 1);
    return Cursor.builder()
        .score(0L)
        .createdAt(lastProject.createdAt().toInstant(ZoneOffset.UTC))
        .updatedAt(lastProject.updatedAt().toInstant(ZoneOffset.UTC))
        .id(lastProject.id())
        .build();
  }
}
