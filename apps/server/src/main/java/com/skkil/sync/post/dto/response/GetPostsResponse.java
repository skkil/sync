package com.skkil.sync.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.post.dto.summary.PostSummary;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  // TODO: Use a preview content string and put that within the PostSummary
  public static record Post(PostSummary summary, String content) {}
}
