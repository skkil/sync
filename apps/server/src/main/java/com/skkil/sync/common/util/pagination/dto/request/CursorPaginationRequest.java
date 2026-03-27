package com.skkil.sync.common.util.pagination.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CursorPaginationRequest(String cursor, @NotNull @Min(1) Integer size) {}
