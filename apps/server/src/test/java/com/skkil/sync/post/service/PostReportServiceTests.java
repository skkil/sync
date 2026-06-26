package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.request.ReportPostRequest;
import com.skkil.sync.post.exception.PostReportAlreadyExistsException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostReport;
import com.skkil.sync.post.model.PostReportReason;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.post.repository.PostReportRepository;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class PostReportServiceTests {

  @Mock private PostRepository postRepository;
  @Mock private PostReportRepository postReportRepository;
  @Mock private UserDomainService userDomainService;
  @Mock private PaginationService paginationService;

  private PostReportService postReportService;

  @BeforeEach
  void setUp() {
    postReportService =
        new PostReportService(
            postRepository, postReportRepository, userDomainService, paginationService);
  }

  @Test
  @DisplayName("[reportPost] 중복 신고 저장 충돌은 PostReportAlreadyExistsException 예외로 변환")
  void reportPost_duplicateConstraintViolation_throwsPostReportAlreadyExistsException() {
    Long reporterId = 1L;
    Long postId = 2L;
    Post post =
        Post.builder()
            .slug("post-slug")
            .author(new User(3L))
            .content("{}")
            .type(PostType.SHORT)
            .build();
    post.setId(postId);
    ReportPostRequest request = new ReportPostRequest(PostReportReason.SPAM, "spam");

    when(postRepository.findByIdAndVisibility(postId, PostVisibility.VISIBLE))
        .thenReturn(Optional.of(post));
    when(postReportRepository.existsByPostIdAndReporterId(postId, reporterId)).thenReturn(false);
    when(userDomainService.getUserReference(reporterId)).thenReturn(new User(reporterId));
    when(postReportRepository.saveAndFlush(any(PostReport.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate report"));

    assertThatThrownBy(() -> postReportService.reportPost(reporterId, postId, request))
        .isInstanceOf(PostReportAlreadyExistsException.class);
  }
}
