package com.skkil.sync.search.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

public record SearchResponse(Page<Result> results) {

  @Builder
  public static record Result(Long id, String name) {}
}
