package com.skkil.sync.post.service.recommendation;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationCursor;

/**
 * 게시글 추천 후보를 커서 기반으로 조회하는 채널. 새로운 추천 방식을 추가하려면 이 인터페이스를 구현하고, {@link
 * com.skkil.sync.post.service.PostRecommendationService}에 주입되는 채널의 {@code @Qualifier}만 교체하면 된다.
 */
public interface PostRecommendationChannel {

  CursorPaginationDataFetcher<PostRecommendationCandidate> getCandidateFetcher(Long requesterId);

  KeysetCursorPaginationProvider<PostRecommendationCandidate, PostRecommendationCursor>
      getPaginationProvider();
}
