package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;

public record GetPostRecommendationsResponse(
    CursorPaginationResponse<GetPostsResponse.Post> posts) {}
