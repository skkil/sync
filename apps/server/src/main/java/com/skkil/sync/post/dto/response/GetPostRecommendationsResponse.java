package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.post.dto.summary.PostSummary;

public record GetPostRecommendationsResponse(CursorPaginationResponse<PostSummary> posts) {}
