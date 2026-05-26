package com.skkil.sync.reflection.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.reflection.dto.response.GetSummariesResponse;
import com.skkil.sync.reflection.mapper.ReflectionSummaryMapper;
import com.skkil.sync.reflection.repository.ReflectionSummaryQueryRepository;
import com.skkil.sync.reflection.repository.pagination.SummaryCursorPaginationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionSummaryQueryService {

  private final ReflectionSummaryQueryRepository reflectionSummaryQueryRepository;
  private final ReflectionSummaryMapper reflectionSummaryMapper;
  private final SummaryCursorPaginationProvider paginationProvider;
  private final PaginationService paginationService;

  public ReflectionSummaryQueryService(
      ReflectionSummaryQueryRepository reflectionSummaryQueryRepository,
      ReflectionSummaryMapper reflectionSummaryMapper,
      SummaryCursorPaginationProvider paginationProvider,
      PaginationService paginationService) {
    this.reflectionSummaryQueryRepository = reflectionSummaryQueryRepository;
    this.reflectionSummaryMapper = reflectionSummaryMapper;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetSummariesResponse getSummaries(Long authorId, CursorPaginationRequest pagination) {
    var summaries =
        paginationService
            .paginate(
                reflectionSummaryQueryRepository.getSummaries(authorId),
                paginationProvider,
                pagination)
            .map(reflectionSummaryMapper::toSummaryResponse);

    return new GetSummariesResponse(summaries);
  }
}
