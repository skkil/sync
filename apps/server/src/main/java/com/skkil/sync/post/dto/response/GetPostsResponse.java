package com.skkil.sync.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.post.dto.summary.PostSummary;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostsResponse(CursorPaginationResponse<PostSummary> posts) {}
