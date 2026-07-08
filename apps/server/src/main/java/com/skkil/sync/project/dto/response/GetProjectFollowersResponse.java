package com.skkil.sync.project.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.user.dto.summary.UserSummary;

public record GetProjectFollowersResponse(CursorPaginationResponse<Follower> followers) {

  public record Follower(UserSummary user) {}
}
