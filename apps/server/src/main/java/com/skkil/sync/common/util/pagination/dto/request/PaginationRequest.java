package com.skkil.sync.common.util.pagination.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public record PaginationRequest(Integer page, @NotNull @Min(1) Integer size) {

  public Pageable toPageable() {
    int pageIndex = page != null ? page : 0;
    return Pageable.ofSize(size).withPage(pageIndex);
  }
}
