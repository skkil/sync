package com.skkil.sync.user.service.recommendation;

import com.skkil.sync.common.recommendation.channel.SimpleRecommendationChannel;
import com.skkil.sync.user.model.UserRecommendationType;

public interface UserRecommendationChannel
    extends SimpleRecommendationChannel<UserRecommendationType, Long> {}
