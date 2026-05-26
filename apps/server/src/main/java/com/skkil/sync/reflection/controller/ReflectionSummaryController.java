package com.skkil.sync.reflection.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.reflection.dto.response.GetSummariesResponse;
import com.skkil.sync.reflection.service.ReflectionSummaryQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReflectionSummaryController {

  private final ReflectionSummaryQueryService reflectionSummaryQueryService;

  public ReflectionSummaryController(ReflectionSummaryQueryService reflectionSummaryQueryService) {
    this.reflectionSummaryQueryService = reflectionSummaryQueryService;
  }

  @GetMapping("/summaries")
  @ResponseStatus(HttpStatus.OK)
  public GetSummariesResponse getSummaries(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return reflectionSummaryQueryService.getSummaries(user.userId(), pagination);
  }
}
