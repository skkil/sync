package com.skkil.sync.provider.contest.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.PaginationRequest;
import com.skkil.sync.provider.contest.dto.request.CreateContestOccurrenceRequest;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrenceResponse;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrencesResponse;
import com.skkil.sync.provider.contest.service.ContestOccurrenceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContestOccurrenceController {

  private final ContestOccurrenceService contestOccurrenceService;

  public ContestOccurrenceController(ContestOccurrenceService contestOccurrenceService) {
    this.contestOccurrenceService = contestOccurrenceService;
  }

  @PostMapping("/contests/{contestId}/occurrences")
  @ResponseStatus(HttpStatus.CREATED)
  public void createContestOccurrence(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long contestId,
      @RequestBody @Validated CreateContestOccurrenceRequest request) {
    contestOccurrenceService.createContestOccurrence(contestId, request);
  }

  @GetMapping("/contests/{contestId}/occurrences/{occurrenceId}")
  public GetContestOccurrenceResponse getContestOccurrence(
      @PathVariable Long contestId, @PathVariable Long occurrenceId) {
    return contestOccurrenceService.getContestOccurrence(contestId, occurrenceId);
  }

  @GetMapping("/contests/{contestId}/occurrences")
  public GetContestOccurrencesResponse getContestOccurrencesByContest(
      @PathVariable Long contestId, @Validated PaginationRequest pagination) {
    return contestOccurrenceService.getContestOccurrencesByContest(contestId, pagination);
  }
}
