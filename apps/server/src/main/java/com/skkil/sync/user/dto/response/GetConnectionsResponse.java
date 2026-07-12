package com.skkil.sync.user.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.user.dto.summary.UserSummary;

public record GetConnectionsResponse(CursorPaginationResponse<UserSummary> connections) {}
