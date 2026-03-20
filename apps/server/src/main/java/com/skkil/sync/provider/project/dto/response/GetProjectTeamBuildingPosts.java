package com.skkil.sync.provider.project.dto.response;

import java.util.List;
import lombok.Builder;

public record GetProjectTeamBuildingPosts(List<Post> posts) {

  @Builder
  public static record Post(String id, String title, String content) {}
}
