package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.post.dto.summary.TagSummary;

public record GetAllTagsResponse(OffsetPaginationResponse<TagSummary> tags) {}
