package com.skkil.sync.common.util.pagination.dto.request;

import com.skkil.sync.common.util.pagination.constants.PaginationDefaultValues;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record OffsetPaginationRequest(
    @Min(0) Integer page, @Min(1) @Max(PaginationDefaultValues.MAXIMUM_PAGE_SIZE) Integer size) {

  public OffsetPaginationRequest {
    if (page == null) {
      page = 0;
    }

    if (size == null) {
      size = PaginationDefaultValues.DEFAULT_PAGE_SIZE;
    }
  }
}
