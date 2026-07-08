package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.response.GetPostReportsResponse;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostReport;
import com.skkil.sync.user.dto.summary.UserSummary;
import com.skkil.sync.user.model.User;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PostReportAssembler {

  public GetPostReportsResponse.Report toReport(
      PostReport report, Map<Long, UserSummary> userSummaries) {
    Post post = report.getPost();
    User reviewer = report.getReviewedBy();

    return new GetPostReportsResponse.Report(
        report.getId(),
        toPost(post),
        userSummaries.get(report.getReporter().getId()),
        report.getReason(),
        report.getDescription(),
        report.getStatus(),
        report.getCreatedAt(),
        reviewer == null ? null : userSummaries.get(reviewer.getId()),
        report.getReviewedAt(),
        report.getResolutionNote());
  }

  private GetPostReportsResponse.Post toPost(Post post) {
    return new GetPostReportsResponse.Post(
        post.getId(), post.getSlug(), post.getTitle(), post.getContent(), post.getVisibility());
  }
}
