package com.skkil.sync.post.service;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.dto.request.AddPostToPostSeriesRequest;
import com.skkil.sync.post.dto.request.CreatePostSeriesRequest;
import com.skkil.sync.post.dto.request.ReorderPostSeriesPostRequest;
import com.skkil.sync.post.dto.request.UpdatePostSeriesRequest;
import com.skkil.sync.post.dto.response.CreatePostSeriesResponse;
import com.skkil.sync.post.dto.response.GetPostSeriesListResponse;
import com.skkil.sync.post.dto.response.GetPostSeriesResponse;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.exception.PostSeriesNotFoundException;
import com.skkil.sync.post.exception.PostSeriesPostAlreadyExistsException;
import com.skkil.sync.post.exception.PostSeriesPostLimitExceededException;
import com.skkil.sync.post.exception.PostSeriesPostNotFoundException;
import com.skkil.sync.post.exception.PostSeriesPostOwnershipMismatchException;
import com.skkil.sync.post.mapper.PostSeriesAssembler;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostSeries;
import com.skkil.sync.post.model.PostSeriesPost;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.repository.PostSeriesPostRepository;
import com.skkil.sync.post.repository.PostSeriesRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostSeriesService {

  private final UserDomainService userDomainService;
  private final ProjectDomainService projectDomainService;
  private final PostDomainService postDomainService;

  private final PostRepository postRepository;
  private final PostSeriesRepository seriesRepository;
  private final PostSeriesPostRepository seriesPostRepository;

  private final PostSeriesAssembler seriesAssembler;

  public PostSeriesService(
      UserDomainService userDomainService,
      ProjectDomainService projectDomainService,
      PostDomainService postDomainService,
      PostRepository postRepository,
      PostSeriesRepository seriesRepository,
      PostSeriesPostRepository seriesPostRepository,
      PostSeriesAssembler seriesAssembler) {
    this.userDomainService = userDomainService;
    this.projectDomainService = projectDomainService;
    this.postDomainService = postDomainService;
    this.postRepository = postRepository;
    this.seriesRepository = seriesRepository;
    this.seriesPostRepository = seriesPostRepository;
    this.seriesAssembler = seriesAssembler;
  }

  @Transactional
  public CreatePostSeriesResponse createPersonalSeries(
      Long userId, CreatePostSeriesRequest request) {
    User creator = userDomainService.getUserReference(userId);

    PostSeries series =
        PostSeries.builder()
            .externalId(Slugify.slugify(request.name()))
            .creator(creator)
            .name(request.name())
            .build();

    return new CreatePostSeriesResponse(seriesRepository.save(series).getExternalId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'CREATE')")
  public CreatePostSeriesResponse createProjectSeries(
      Long userId, String handle, CreatePostSeriesRequest request) {
    User creator = userDomainService.getUserReference(userId);
    Project project = projectDomainService.getProjectByHandle(handle);

    PostSeries series =
        PostSeries.builder()
            .externalId(Slugify.slugify(request.name()))
            .creator(creator)
            .project(project)
            .name(request.name())
            .build();

    return new CreatePostSeriesResponse(seriesRepository.save(series).getExternalId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#externalId, 'POST_SERIES', 'EDIT')")
  public void updateSeries(String externalId, UpdatePostSeriesRequest request) {
    PostSeries series = getSeriesByExternalId(externalId);

    String name = request.name() == null ? series.getName() : request.name();

    series.update(name);
  }

  @Transactional
  @PreAuthorize("hasPermission(#externalId, 'POST_SERIES', 'DELETE')")
  public void deleteSeries(String externalId) {
    PostSeries series = getSeriesByExternalId(externalId);
    // post_series_posts 는 cascade 로 지워지지만 posts.is_series_post 는 자동 갱신되지 않으므로,
    // 삭제 전에 소속 게시글들의 플래그를 일괄 해제한다.
    postRepository.clearSeriesFlagBySeriesId(series.getId());
    seriesRepository.delete(series);
  }

  @Transactional
  @PreAuthorize("hasPermission(#externalId, 'POST_SERIES', 'CREATE')")
  public void addPost(Long userId, String externalId, AddPostToPostSeriesRequest request) {
    PostSeries series = getSeriesByExternalId(externalId);

    if (seriesPostRepository.countBySeriesId(series.getId())
        >= PostConstants.MAX_POSTS_PER_SERIES) {
      throw new PostSeriesPostLimitExceededException();
    }

    Post post = resolvePost(userId, request.projectHandle(), request.postHandle());
    Long postId = post.getId();

    // 한 게시글은 최대 하나의 시리즈에만 속한다.
    if (seriesPostRepository.existsBySeriesIdAndPostId(series.getId(), postId)) {
      throw new PostSeriesPostAlreadyExistsException(series.getId(), postId);
    }
    if (seriesPostRepository.existsByPostId(postId)) {
      throw new PostSeriesPostAlreadyExistsException(postId);
    }

    verifyOwnership(userId, series, post);

    int maxPosition = seriesPostRepository.findMaxPosition(series.getId());
    int position;
    if (request.position() == null) {
      position = maxPosition + 1;
    } else {
      position = clamp(request.position(), 1, maxPosition + 1);
      seriesPostRepository.shiftUpFrom(series.getId(), position);
    }

    seriesPostRepository.save(
        PostSeriesPost.builder().series(series).post(post).position(position).build());
    post.markInSeries();
  }

  @Transactional
  @PreAuthorize("hasPermission(#externalId, 'POST_SERIES', 'EDIT')")
  public void reorderPost(
      String externalId, Long seriesPostId, ReorderPostSeriesPostRequest request) {
    PostSeries series = getSeriesByExternalId(externalId);
    PostSeriesPost seriesPost = getSeriesPost(series, seriesPostId);

    int oldPosition = seriesPost.getPosition();
    int maxPosition = seriesPostRepository.findMaxPosition(series.getId());
    int newPosition = clamp(request.position(), 1, maxPosition);
    if (newPosition == oldPosition) {
      return;
    }

    if (newPosition < oldPosition) {
      // 앞으로 이동: 사이 항목을 뒤로 민다.
      seriesPostRepository.shiftUpBetween(series.getId(), newPosition, oldPosition);
    } else {
      // 뒤로 이동: 사이 항목을 앞으로 당긴다.
      seriesPostRepository.shiftDownBetween(series.getId(), oldPosition, newPosition);
    }
    seriesPost.updatePosition(newPosition);
  }

  @Transactional
  @PreAuthorize("hasPermission(#externalId, 'POST_SERIES', 'EDIT')")
  public void removeItem(String externalId, Long seriesPostId) {
    PostSeries series = getSeriesByExternalId(externalId);
    PostSeriesPost seriesPost = getSeriesPost(series, seriesPostId);

    Post post = seriesPost.getPost();
    int position = seriesPost.getPosition();
    seriesPostRepository.delete(seriesPost);
    seriesPostRepository.shiftDownAfter(series.getId(), position);
    post.unmarkInSeries();
  }

  /** 로그인 사용자가 만든 개인 시리즈 목록. 시리즈는 저자 본인이 자기 글을 엮는 묶음이므로 본인 소유 시리즈만 노출한다. */
  @Transactional(readOnly = true)
  public GetPostSeriesListResponse getMyPersonalSeries(Long userId) {
    List<PostSeries> series =
        seriesRepository.findByCreatorId(userId).stream().filter(PostSeries::isPersonal).toList();
    return seriesAssembler.toGetSeriesListResponse(series);
  }

  /** 로그인 사용자가 특정 프로젝트에서 만든 워크스페이스 시리즈 목록. 개인 컨텍스트와 동일하게 본인 소유 시리즈만 노출한다. */
  @Transactional(readOnly = true)
  public GetPostSeriesListResponse getMyProjectSeries(Long userId, String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);
    List<PostSeries> series = seriesRepository.findByProjectIdAndCreatorId(project.getId(), userId);
    return seriesAssembler.toGetSeriesListResponse(series);
  }

  /**
   * 한 게시글이 속한 시리즈와 그 시리즈의 게시글 목록을 함께 조회한다. 게시글 상세 페이지의 시리즈 내비게이션에 쓴다. 한 게시글은 최대 하나의 시리즈에만 속하므로 시리즈는
   * 최대 하나이며, 어떤 시리즈에도 속하지 않으면 {@code series} 와 {@code currentSeriesPostId} 는 {@code null}, {@code
   * posts} 는 빈 목록이다. {@code currentSeriesPostId} 로 목록 안에서 현재 글이 어느 항목인지 식별한다. 게시글 자체를 열람할 수 없으면 존재를
   * 숨기기 위해 404 를 던진다(다른 읽기 경로와 동일한 게이트). 시리즈 항목 중 요청자가 볼 수 없는 게시글은 불투명 slot(title == null) 으로 노출된다.
   */
  @Transactional(readOnly = true)
  public GetPostSeriesResponse getSeriesForPost(String slug, @Nullable AuthenticatedUser viewer) {
    Long requesterId = viewer == null ? null : viewer.userId();

    Post post = postDomainService.getPostBySlug(slug);
    if (!postDomainService.isPostReadable(requesterId, post.getId())) {
      throw new PostNotFoundException(slug);
    }

    return seriesPostRepository
        .findByPostId(post.getId())
        .map(
            seriesPost -> {
              PostSeries series = seriesPost.getSeries();
              List<PostSeriesPost> items =
                  seriesPostRepository.findBySeriesIdOrderByPositionAscIdAsc(series.getId());
              return new GetPostSeriesResponse(
                  seriesAssembler.toSummary(series, items.size()),
                  seriesPost.getId(),
                  seriesAssembler.toSeriesPostItems(items, requesterId));
            })
        .orElseGet(() -> new GetPostSeriesResponse(null, null, List.of()));
  }

  private Post resolvePost(Long userId, @Nullable String projectHandle, String postHandle) {
    Post post = postDomainService.getPostBySlug(postHandle);

    String actualProjectHandle = post.getProject() == null ? null : post.getProject().getHandle();
    if (!Objects.equals(actualProjectHandle, projectHandle)) {
      throw new PostNotFoundException(postHandle);
    }

    // 읽기 경로와 동일한 단일 열람 게이트. 볼 수 없는 게시글은 없는 것과 구분되지 않게 한다.
    if (!postDomainService.isPostReadable(userId, post.getId())) {
      throw new PostNotFoundException(postHandle);
    }

    return post;
  }

  /**
   * 시리즈는 저자 본인이 자기 글로 엮는 묶음이다. 따라서 게시글을 넣는 사람은 그 게시글의 저자여야 하며(요청자 == 저자), 남의 글을 대신 넣을 수 없다. 추가로
   * 워크스페이스 시리즈에는 같은 프로젝트의 게시글만 담을 수 있다.
   */
  private void verifyOwnership(Long userId, PostSeries series, Post post) {
    // 게시글을 시리즈에 넣을 수 있는 사람은 그 게시글의 저자뿐이다.
    if (!post.getAuthor().getId().equals(userId)) {
      throw new PostSeriesPostOwnershipMismatchException(series.getId(), post.getId());
    }

    // 한 시리즈는 단일 프로젝트로 묶인다 — 서로 다른 프로젝트의 게시글을 섞을 수 없다. 게시글의 프로젝트가 시리즈의 프로젝트와 정확히 일치해야 한다.
    // 워크스페이스 시리즈는 해당 프로젝트의 게시글만, 개인 시리즈(프로젝트 없음)는 프로젝트 없는 개인 게시글만 담을 수 있다.
    Long seriesProjectId = series.getProject() == null ? null : series.getProject().getId();
    Long postProjectId = post.getProject() == null ? null : post.getProject().getId();
    if (!Objects.equals(seriesProjectId, postProjectId)) {
      throw new PostSeriesPostOwnershipMismatchException(series.getId(), post.getId());
    }
  }

  private PostSeriesPost getSeriesPost(PostSeries series, Long seriesPostId) {
    return seriesPostRepository
        .findByIdAndSeriesId(seriesPostId, series.getId())
        .orElseThrow(() -> new PostSeriesPostNotFoundException(series.getId(), seriesPostId));
  }

  private PostSeries getSeriesByExternalId(String externalId) {
    return seriesRepository
        .findByExternalId(externalId)
        .orElseThrow(() -> new PostSeriesNotFoundException(externalId));
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(value, max));
  }
}
