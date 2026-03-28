package com.skkil.sync.search.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

public record SearchResponse(Page<Result> results, Count count) {

  @Builder
  public static record Result(Long id, String name) {}

  @Builder
  public static record Count(
      long userCount, long schoolCount, long companyCount, long contestCount, long projectCount) {}
}
