package com.skkil.sync.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skkil.sync.post.model.PostType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostResponse(
    Long id,
    PostType type,
    String slug,
    Author author,
    @Nullable Project project,
    Content content,
    Long likeCount,
    Long commentCount,
    boolean bookmarked,
    LocalDateTime createdAt) {

  @Builder
  public static record Author(String name, String handle) {}

  @Builder
  public static record Project(Long id, String name) {}

  @Builder
  public static record Content(String json, List<Media> media) {}

  @Builder
  public static record Media(Long id, String url) {}
}
