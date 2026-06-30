package com.skkil.sync.post.dto.response;

import java.util.List;
import lombok.Builder;

public record SearchPostsResponse(List<Post> posts) {

  @Builder
  public static record Post(Long id, String content) {}
}
