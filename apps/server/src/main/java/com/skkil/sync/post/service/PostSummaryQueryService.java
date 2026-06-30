package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.response.GetSummariesResponse;
import com.skkil.sync.post.mapper.PostSummaryMapper;
import com.skkil.sync.post.repository.PostSummaryQueryRepository;
import com.skkil.sync.post.repository.pagination.SummaryCursorPaginationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostSummaryQueryService {

  private final PostSummaryQueryRepository postSummaryQueryRepository;
  private final PostSummaryMapper postSummaryMapper;
  private final SummaryCursorPaginationProvider paginationProvider;
  private final PaginationService paginationService;

  public PostSummaryQueryService(
      PostSummaryQueryRepository postSummaryQueryRepository,
      PostSummaryMapper postSummaryMapper,
      SummaryCursorPaginationProvider paginationProvider,
      PaginationService paginationService) {
    this.postSummaryQueryRepository = postSummaryQueryRepository;
    this.postSummaryMapper = postSummaryMapper;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetSummariesResponse getSummaries(Long authorId, CursorPaginationRequest pagination) {
    var summaries =
        paginationService
            .paginate(
                postSummaryQueryRepository.getSummaries(authorId), paginationProvider, pagination)
            .map(postSummaryMapper::toSummaryResponse);

    return new GetSummariesResponse(summaries);
  }
}
