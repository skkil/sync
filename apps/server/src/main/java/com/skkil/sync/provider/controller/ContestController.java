package com.skkil.sync.provider.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.provider.dto.request.CreateContestOccurrenceRequest;
import com.skkil.sync.provider.service.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContestController {

  private final ContestService contestService;

  public ContestController(ContestService contestService) {
    this.contestService = contestService;
  }

  @PostMapping("/contests/{contestId}/occurrences")
  @ResponseStatus(HttpStatus.CREATED)
  public void createContestOccurrence(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long contestId,
      @RequestBody @Validated CreateContestOccurrenceRequest request) {
    contestService.createContestOccurrence(contestId, request);
  }
}
