package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.request.ReportPostRequest;
import com.skkil.sync.post.dto.request.ReviewPostReportRequest;
import com.skkil.sync.post.dto.response.GetPostReportsResponse;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.exception.PostReportAlreadyExistsException;
import com.skkil.sync.post.exception.PostReportAlreadyReviewedException;
import com.skkil.sync.post.exception.PostReportNotFoundException;
import com.skkil.sync.post.mapper.PostReportAssembler;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostReport;
import com.skkil.sync.post.model.PostReportResolution;
import com.skkil.sync.post.model.PostReportStatus;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.post.repository.PostReportRepository;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.user.dto.summary.UserSummary;
import com.skkil.sync.user.mapper.UserAssembler;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostReportService {

  private final PostRepository postRepository;
  private final PostReportRepository postReportRepository;
  private final UserDomainService userDomainService;
  private final UserAssembler userAssembler;
  private final PostReportAssembler postReportAssembler;
  private final PaginationService paginationService;

  public PostReportService(
      PostRepository postRepository,
      PostReportRepository postReportRepository,
      UserDomainService userDomainService,
      UserAssembler userAssembler,
      PostReportAssembler postReportAssembler,
      PaginationService paginationService) {
    this.postRepository = postRepository;
    this.postReportRepository = postReportRepository;
    this.userDomainService = userDomainService;
    this.userAssembler = userAssembler;
    this.postReportAssembler = postReportAssembler;
    this.paginationService = paginationService;
  }

  @Transactional
  public void reportPost(Long reporterId, Long postId, ReportPostRequest request) {
    Post post =
        postRepository
            .findByIdAndVisibility(postId, PostVisibility.VISIBLE)
            .orElseThrow(() -> new PostNotFoundException(postId));

    if (postReportRepository.existsByPostIdAndReporterId(postId, reporterId)) {
      throw new PostReportAlreadyExistsException(postId, reporterId);
    }

    User reporter = userDomainService.getUserReference(reporterId);
    try {
      postReportRepository.saveAndFlush(
          new PostReport(post, reporter, request.reason(), request.description()));
    } catch (DataIntegrityViolationException e) {
      throw new PostReportAlreadyExistsException(postId, reporterId, e);
    }
  }

  @Transactional(readOnly = true)
  public GetPostReportsResponse getReports(
      PostReportStatus status, OffsetPaginationRequest pagination) {
    OffsetPaginationResponse<PostReport> page =
        paginationService.paginate(
            pageable ->
                status == null
                    ? postReportRepository.findAllByOrderByCreatedAtDescIdDesc(pageable)
                    : postReportRepository.findByStatusOrderByCreatedAtDescIdDesc(status, pageable),
            pagination);

    var userIds =
        page.content().stream()
            .flatMap(
                report ->
                    Stream.of(
                        report.getReporter().getId(),
                        report.getReviewedBy() == null ? null : report.getReviewedBy().getId()))
            .filter(Objects::nonNull)
            .toList();
    Map<Long, UserSummary> userSummaries = userAssembler.toUserSummaries(userIds);

    OffsetPaginationResponse<GetPostReportsResponse.Report> reports =
        page.map(report -> postReportAssembler.toReport(report, userSummaries));

    return new GetPostReportsResponse(reports);
  }

  @Transactional
  public void reviewReport(Long reviewerId, Long reportId, ReviewPostReportRequest request) {
    PostReport report =
        postReportRepository
            .findById(reportId)
            .orElseThrow(() -> new PostReportNotFoundException(reportId));

    if (report.isReviewed()) {
      throw new PostReportAlreadyReviewedException(reportId);
    }

    User reviewer = userDomainService.getUserReference(reviewerId);

    if (request.resolution() == PostReportResolution.DISMISS) {
      report.dismiss(reviewer, request.resolutionNote());
      return;
    }

    Post post = report.getPost();
    post.hide(reviewer, hiddenReason(request));

    postReportRepository
        .findByPostIdAndStatus(post.getId(), PostReportStatus.PENDING)
        .forEach(pendingReport -> pendingReport.resolve(reviewer, request.resolutionNote()));
  }

  private String hiddenReason(ReviewPostReportRequest request) {
    if (request.hiddenReason() != null && !request.hiddenReason().isBlank()) {
      return request.hiddenReason();
    }

    return request.resolutionNote();
  }
}
