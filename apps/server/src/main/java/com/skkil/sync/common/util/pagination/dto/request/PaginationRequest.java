package com.skkil.sync.common.util.pagination.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaginationRequest(Integer page, @NotNull @Min(1) Integer size) {}
