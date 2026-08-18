package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.response.GetPostSeriesListResponse;
import com.skkil.sync.post.dto.response.GetPostSeriesResponse;
import com.skkil.sync.post.dto.summary.PostSeriesSummary;
import com.skkil.sync.post.dto.summary.PostSummary;
import com.skkil.sync.post.model.PostSeries;
import com.skkil.sync.post.model.PostSeriesPost;
import com.skkil.sync.post.repository.PostSeriesPostRepository;
import com.skkil.sync.post.repository.PostSeriesPostRepository.SeriesPostCount;
import com.skkil.sync.post.service.PostDomainService;
import com.skkil.sync.project.model.Project;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class PostSeriesAssembler {

  private final PostDomainService postDomainService;
  private final PostSeriesPostRepository seriesPostRepository;

  public PostSeriesAssembler(
      PostDomainService postDomainService, PostSeriesPostRepository seriesPostRepository) {
    this.postDomainService = postDomainService;
    this.seriesPostRepository = seriesPostRepository;
  }

  /**
   * {@code postCount} 는 저장하지 않고 {@code post_series_posts} 행 수에서 파생한다. 게시글 삭제처럼 FK cascade 로 행이 사라지는
   * 경로는 JPA 를 타지 않아 저장된 카운터가 조용히 어긋나기 때문이다. 호출자가 이미 행 수를 알고 있으면 그 값을 그대로 넘긴다.
   */
  public PostSeriesSummary toSummary(PostSeries series, long postCount) {
    Project project = series.getProject();
    return new PostSeriesSummary(
        series.getExternalId(),
        series.getName(),
        series.getScope(),
        postCount,
        series.getCreator().getId(),
        project == null ? null : project.getHandle());
  }

  public GetPostSeriesListResponse toGetSeriesListResponse(List<PostSeries> seriesList) {
    Map<Long, Long> counts = countsBySeriesId(seriesList);
    return new GetPostSeriesListResponse(
        seriesList.stream()
            .map(series -> toSummary(series, counts.getOrDefault(series.getId(), 0L)))
            .toList());
  }

  private Map<Long, Long> countsBySeriesId(List<PostSeries> seriesList) {
    if (seriesList.isEmpty()) {
      return Map.of();
    }

    List<Long> seriesIds = seriesList.stream().map(PostSeries::getId).toList();
    return seriesPostRepository.countBySeriesIds(seriesIds).stream()
        .collect(Collectors.toMap(SeriesPostCount::getSeriesId, SeriesPostCount::getPostCount));
  }

  public List<GetPostSeriesResponse.Post> toSeriesPostItems(
      List<PostSeriesPost> items, @Nullable Long requesterId) {
    List<Long> postIds = items.stream().map(item -> item.getPost().getId()).toList();
    Map<Long, PostSummary> visibleSummaries =
        postDomainService.getReadablePostSummaries(requesterId, postIds);

    // 표시 순번은 저장된 position 이 아니라 정렬된 목록의 차례에서 파생한다. 우회 삭제 경로로
    // 저장값에 구멍이 생겨도 사용자에게는 항상 1부터 연속된 번호로 보인다.
    return IntStream.range(0, items.size())
        .mapToObj(index -> toItem(items.get(index), index + 1, visibleSummaries))
        .toList();
  }

  private GetPostSeriesResponse.Post toItem(
      PostSeriesPost item, int position, Map<Long, PostSummary> visibleSummaries) {
    PostSummary summary = visibleSummaries.get(item.getPost().getId());
    // 열람할 수 없는 항목은 불투명 slot 이므로 제목뿐 아니라 이동 경로(slug)도 노출하지 않는다.
    String slug = summary == null ? null : summary.slug();
    String title = summary == null ? null : summary.title();
    return new GetPostSeriesResponse.Post(item.getId(), position, slug, title);
  }
}
