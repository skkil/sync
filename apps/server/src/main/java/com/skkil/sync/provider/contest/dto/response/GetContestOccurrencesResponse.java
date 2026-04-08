package com.skkil.sync.provider.contest.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.PaginationResponse;

public record GetContestOccurrencesResponse(PaginationResponse<ContestOccurrence> occurrences) {

  public static record ContestOccurrence(
      Long id, Contest contest, String title, String description) {}

  public static record Contest(Long id, String name) {}
}
