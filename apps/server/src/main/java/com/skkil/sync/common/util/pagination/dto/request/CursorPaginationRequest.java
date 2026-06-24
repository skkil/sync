package com.skkil.sync.common.util.pagination.dto.request;

import com.skkil.sync.common.util.pagination.constants.PaginationDefaultValues;
import com.skkil.sync.common.util.pagination.exception.InvalidPaginationParametersException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CursorPaginationRequest(
    @Min(1) @Max(PaginationDefaultValues.MAXIMUM_PAGE_SIZE) Integer first,
    String after,
    @Min(1) @Max(PaginationDefaultValues.MAXIMUM_PAGE_SIZE) Integer last,
    String before) {

  public CursorPaginationRequest {
    if (first != null && last != null) {
      throw new InvalidPaginationParametersException(
          "Cannot specify both 'first' and 'last' parameters.");
    }

    if (first != null && before != null) {
      throw new InvalidPaginationParametersException(
          "Cannot specify 'before' when using 'first'. Use 'last' and 'before' together.");
    }

    if (last != null && after != null) {
      throw new InvalidPaginationParametersException(
          "Cannot specify 'after' when using 'last'. Use 'first' and 'after' together.");
    }

    if (first == null && last == null) {
      if (before != null) {
        throw new InvalidPaginationParametersException("'before' must be used with 'last'.");
      }

      first = PaginationDefaultValues.DEFAULT_PAGE_SIZE;
    }
  }

  public boolean isForward() {
    return first != null;
  }
}
