package com.skkil.sync.provider.project.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;

public record GetProjectsResponse(CursorPaginationResponse<Project> projects) {

  public static record Project(Long id, String name, String description) {}
}
