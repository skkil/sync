package com.skkil.sync.project.service.recommendation;

import com.skkil.sync.common.recommendation.channel.SimpleRecommendationChannel;
import com.skkil.sync.project.model.ProjectRecommendationType;

public interface ProjectRecommendationChannel
    extends SimpleRecommendationChannel<ProjectRecommendationType, Long> {}
