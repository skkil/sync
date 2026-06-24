package com.skkil.sync.post.dto.response;

import com.skkil.sync.post.model.PostType;
import java.util.List;
import lombok.Builder;

@Builder
public record GetPostResponse(
    Long id,
    PostType type,
    String slug,
    Author author,
    Project project,
    Content content,
    Long likeCount,
    Long commentCount,
    boolean bookmarked) {

  @Builder
  public static record Author(Long id, String name) {}

  @Builder
  public static record Project(Long id, String name) {}

  @Builder
  public static record Content(String json, List<Media> media) {}

  @Builder
  public static record Media(Long id, String url) {}
}
