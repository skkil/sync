package com.skkil.sync.provider.project.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import lombok.Builder;

public record GetTeamBuildingPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  public static record Post(String id, Project project, String title, String content) {}

  @Builder
  public static record Project(String id, String name, String description) {}
}
