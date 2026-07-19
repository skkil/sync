package com.skkil.sync.project.service;

import com.skkil.sync.common.recommendation.merger.OrderedUnionCandidateMerger;
import com.skkil.sync.common.recommendation.registry.SimpleRecommendationChannelRegistry;
import com.skkil.sync.project.dto.response.GetProjectRecommendationsResponse;
import com.skkil.sync.project.mapper.ProjectAssembler;
import com.skkil.sync.project.model.ProjectRecommendationType;
import com.skkil.sync.project.service.recommendation.ProjectRecommendationChannel;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectRecommendationService {

  public static final int RECOMMENDATION_LIMIT = 15;

  private final SimpleRecommendationChannelRegistry<ProjectRecommendationType, Long>
      channelRegistry;
  private final ProjectAssembler projectAssembler;

  public ProjectRecommendationService(
      List<ProjectRecommendationChannel> channels, ProjectAssembler projectAssembler) {
    this.channelRegistry =
        new SimpleRecommendationChannelRegistry<>(
            ProjectRecommendationType.class,
            ProjectRecommendationType.TRENDING,
            channels,
            new OrderedUnionCandidateMerger<>());
    this.projectAssembler = projectAssembler;
  }

  @Transactional(readOnly = true)
  public GetProjectRecommendationsResponse getRecommendations(
      Long userId, @Nullable ProjectRecommendationType type) {
    var orderedIds =
        (type != null)
            ? channelRegistry.fetch(userId, type, RECOMMENDATION_LIMIT)
            : channelRegistry.fetch(userId, RECOMMENDATION_LIMIT);

    var summaries = projectAssembler.toProjectSummaries(orderedIds);
    var projects = orderedIds.stream().map(summaries::get).filter(Objects::nonNull).toList();

    return new GetProjectRecommendationsResponse(projects);
  }
}
