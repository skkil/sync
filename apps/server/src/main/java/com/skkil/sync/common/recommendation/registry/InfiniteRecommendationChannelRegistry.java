package com.skkil.sync.common.recommendation.registry;

import com.skkil.sync.common.recommendation.channel.InfiniteRecommendationChannel;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class InfiniteRecommendationChannelRegistry<TType extends Enum<TType>, TCandidate>
    extends RecommendationChannelRegistry<
        TType, InfiniteRecommendationChannel<TType, TCandidate, ? extends Cursor>> {

  private final PaginationService paginationService;

  public InfiniteRecommendationChannelRegistry(
      Class<TType> typeClass,
      TType defaultType,
      List<? extends InfiniteRecommendationChannel<TType, TCandidate, ? extends Cursor>> channels,
      PaginationService paginationService) {
    super(typeClass, defaultType, channels);
    this.paginationService = paginationService;
  }

  public CursorPaginationResponse<TCandidate> fetch(
      Long requester, CursorPaginationRequest request) {
    return fetch(requester, defaultType, request);
  }

  public CursorPaginationResponse<TCandidate> fetch(
      Long requester, @Nullable TType type, CursorPaginationRequest request) {
    return paginate(resolve(type), requester, request);
  }

  private <C extends Cursor> CursorPaginationResponse<TCandidate> paginate(
      InfiniteRecommendationChannel<TType, TCandidate, C> channel,
      Long requester,
      CursorPaginationRequest request) {
    return paginationService.paginate(
        channel.getCandidateFetcher(requester), channel.getPaginationProvider(), request);
  }
}
