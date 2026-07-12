package com.skkil.sync.user.mapper;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.user.dto.data.UserConnectionDto;
import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import org.springframework.stereotype.Component;

@Component
public class UserConnectionAssembler {

  private final UserAssembler userAssembler;

  public UserConnectionAssembler(UserAssembler userAssembler) {
    this.userAssembler = userAssembler;
  }

  public GetConnectionsResponse toGetConnectionsResponse(
      CursorPaginationResponse<UserConnectionDto> connections) {
    return new GetConnectionsResponse(
        connections.mapWithLookup(
            UserConnectionDto::userId,
            userAssembler::toUserSummaries,
            (dto, summaries) -> summaries.get(dto.userId())));
  }
}
