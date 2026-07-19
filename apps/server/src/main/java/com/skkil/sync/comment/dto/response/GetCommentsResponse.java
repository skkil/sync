package com.skkil.sync.comment.dto.response;

import com.skkil.sync.comment.dto.summary.CommentSummary;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;

public record GetCommentsResponse(CursorPaginationResponse<CommentSummary> comments) {}
