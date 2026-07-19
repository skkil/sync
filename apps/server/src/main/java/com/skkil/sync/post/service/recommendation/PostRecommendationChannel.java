package com.skkil.sync.post.service.recommendation;

import com.skkil.sync.common.recommendation.channel.InfiniteRecommendationChannel;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationCursor;
import com.skkil.sync.post.model.PostRecommendationType;

public interface PostRecommendationChannel
    extends InfiniteRecommendationChannel<
        PostRecommendationType, PostRecommendationCandidate, PostRecommendationCursor> {}
