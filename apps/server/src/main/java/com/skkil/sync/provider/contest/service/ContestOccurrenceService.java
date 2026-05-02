package com.skkil.sync.provider.contest.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.contest.dto.request.CreateContestOccurrenceRequest;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrenceResponse;
import com.skkil.sync.provider.contest.dto.response.GetContestOccurrencesResponse;
import com.skkil.sync.provider.contest.exception.ContestOccurrenceNotFoundException;
import com.skkil.sync.provider.contest.model.Contest;
import com.skkil.sync.provider.contest.model.ContestOccurrence;
import com.skkil.sync.provider.contest.repository.ContestOccurrenceRepository;
import com.skkil.sync.provider.contest.repository.ContestRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContestOccurrenceService {

  private final ContestRepository contestRepository;
  private final ContestOccurrenceRepository contestOccurrenceRepository;
  private final PaginationService paginationService;

  public ContestOccurrenceService(
      ContestRepository contestRepository,
      ContestOccurrenceRepository contestOccurrenceRepository,
      PaginationService paginationService) {
    this.contestRepository = contestRepository;
    this.contestOccurrenceRepository = contestOccurrenceRepository;
    this.paginationService = paginationService;
  }

  @Transactional
  @PreAuthorize("hasPermission(#contestId, 'PROVIDER', 'EDIT')")
  public void createContestOccurrence(Long contestId, CreateContestOccurrenceRequest request) {
    Contest contest = contestRepository.getReferenceById(contestId);

    ContestOccurrence occurrence =
        ContestOccurrence.builder()
            .contest(contest)
            .title(request.title())
            .description(request.description())
            .build();
    contestOccurrenceRepository.save(occurrence);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#contestId, 'PROVIDER', 'READ')")
  public GetContestOccurrenceResponse getContestOccurrence(Long contestId, Long occurrenceId) {
    ContestOccurrence occurrence =
        contestOccurrenceRepository
            .findByIdAndContestId(occurrenceId, contestId)
            .orElseThrow(() -> new ContestOccurrenceNotFoundException(occurrenceId));

    Contest contest = occurrence.getContest();
    GetContestOccurrenceResponse.Contest contestDto =
        new GetContestOccurrenceResponse.Contest(contest.getId(), contest.getName());

    return new GetContestOccurrenceResponse(
        occurrence.getId(), contestDto, occurrence.getTitle(), occurrence.getDescription());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#contestId, 'PROVIDER', 'READ')")
  public GetContestOccurrencesResponse getContestOccurrencesByContest(
      Long contestId, OffsetPaginationRequest pagination) {
    Contest contest = contestRepository.getReferenceById(contestId);

    var occurrences =
        paginationService
            .paginate(
                pageable -> contestOccurrenceRepository.findByContest(contest, pageable),
                pagination)
            .map(
                occurrence -> {
                  Contest occurrenceContest = occurrence.getContest();
                  GetContestOccurrencesResponse.Contest contestDto =
                      new GetContestOccurrencesResponse.Contest(
                          occurrenceContest.getId(), occurrenceContest.getName());

                  return new GetContestOccurrencesResponse.ContestOccurrence(
                      occurrence.getId(),
                      contestDto,
                      occurrence.getTitle(),
                      occurrence.getDescription());
                });

    return new GetContestOccurrencesResponse(occurrences);
  }
}
