package com.skkil.sync.common.recommendation.channel;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.model.Cursor;

public interface InfiniteRecommendationChannel<
        TType extends Enum<TType>, TCandidate, TCursor extends Cursor>
    extends RecommendationChannel<TType> {

  CursorPaginationDataFetcher<TCandidate> getCandidateFetcher(Long requesterId);

  KeysetCursorPaginationProvider<TCandidate, TCursor> getPaginationProvider();
}
