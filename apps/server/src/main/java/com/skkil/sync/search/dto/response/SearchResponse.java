package com.skkil.sync.search.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import lombok.Builder;

public record SearchResponse(OffsetPaginationResponse<Result> results, Count count) {

  @Builder
  public static record Result(Long id, String name) {}

  @Builder
  public static record Count(
      long userCount, long schoolCount, long companyCount, long contestCount, long projectCount) {}
}
