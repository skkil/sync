package com.skkil.sync.post.service;

import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.post.constants.PostConstants;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.CreateProjectPostRequest;
import com.skkil.sync.post.dto.request.PostContentRequest;
import com.skkil.sync.post.dto.request.UpdatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostSummaryRequest;
import com.skkil.sync.post.dto.request.UpdateProjectPostRequest;
import com.skkil.sync.post.dto.response.CreatePostResponse;
import com.skkil.sync.post.event.PostContentChangedEvent;
import com.skkil.sync.post.event.PostPublishedEvent;
import com.skkil.sync.post.exception.InvalidPostPublishRequestException;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.exception.PostPinLimitExceededException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.repository.PostSeriesPostRepository;
import com.skkil.sync.post.util.PostSlugGenerator;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

  private final UserDomainService userDomainService;
  private final ProjectDomainService projectDomainService;
  private final MediaDomainService mediaDomainService;

  private final TagService tagService;
  private final PostReferenceService postReferenceService;
  private final PostContentMediaService contentMediaService;
  private final ApplicationEventPublisher eventPublisher;

  private final PostRepository postRepository;
  private final PostSeriesPostRepository seriesPostRepository;

  public PostService(
      UserDomainService userDomainService,
      ProjectDomainService projectDomainService,
      MediaDomainService mediaDomainService,
      TagService tagService,
      PostReferenceService postReferenceService,
      PostContentMediaService contentMediaService,
      PostRepository postRepository,
      PostSeriesPostRepository seriesPostRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userDomainService = userDomainService;
    this.projectDomainService = projectDomainService;
    this.mediaDomainService = mediaDomainService;
    this.tagService = tagService;
    this.postReferenceService = postReferenceService;
    this.contentMediaService = contentMediaService;
    this.postRepository = postRepository;
    this.seriesPostRepository = seriesPostRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public CreatePostResponse createPost(Long authorId, CreatePostRequest request) {
    return createPost(
        authorId,
        request.title(),
        request.type(),
        request.status(),
        request.content(),
        request.tags(),
        List.of(),
        request.referencedPostIds(),
        request.coverMediaId(),
        null);
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'CREATE')")
  public CreatePostResponse createProjectPost(
      Long authorId, String handle, CreateProjectPostRequest request) {
    Project project = projectDomainService.getProjectByHandle(handle);

    return createPost(
        authorId,
        request.title(),
        request.type(),
        request.status(),
        request.content(),
        request.tags(),
        request.projectTags(),
        request.referencedPostIds(),
        request.coverMediaId(),
        project);
  }

  private CreatePostResponse createPost(
      Long authorId,
      String title,
      PostType type,
      @Nullable PostStatus status,
      PostContentRequest content,
      List<String> tags,
      @Nullable List<String> projectTags,
      @Nullable List<Long> referencedPostIds,
      @Nullable String coverMediaId,
      @Nullable Project project) {
    status = status == null ? PostStatus.PUBLISHED : status;
    projectTags = projectTags == null ? List.of() : projectTags;

    User author = userDomainService.getUserReference(authorId);
    String slug = PostSlugGenerator.generate(author, title);

    List<Media> mediaFiles =
        contentMediaService.resolveMediaFilesForCreate(authorId, content.mediaIds());

    Media coverMedia = type == PostType.LONG ? resolveCover(authorId, coverMediaId) : null;

    Post.PostBuilder postBuilder =
        Post.builder()
            .slug(slug)
            .author(author)
            .type(type)
            .status(status)
            .title(title)
            .jsonContent(content.json())
            .coverMedia(coverMedia);
    if (project != null) {
      postBuilder.project(project);
    }
    Post post = postBuilder.build();

    post.updateJsonContent(content.json(), content.text(), mediaFiles.size());

    return new CreatePostResponse(
        persistNewPost(
                post, project, tags, projectTags, referencedPostIds, mediaFiles, content.text())
            .getSlug());
  }

  @Transactional
  Post createMarkdownDraft(
      Long authorId,
      @Nullable String title,
      PostType type,
      String bodyMarkdown,
      List<String> tags,
      List<String> projectTags,
      @Nullable Project project,
      String createdViaClientId) {
    User author = userDomainService.getUserReference(authorId);
    String slug = PostSlugGenerator.generate(author, title);

    Post.PostBuilder postBuilder =
        Post.builder()
            .slug(slug)
            .author(author)
            .type(type)
            .status(PostStatus.DRAFT)
            .title(title)
            .createdViaClientId(createdViaClientId);
    if (project != null) {
      postBuilder.project(project);
    }
    Post post = postBuilder.build();

    post.updateMarkdownContent(bodyMarkdown);

    return persistNewPost(post, project, tags, projectTags, null, List.of(), bodyMarkdown);
  }

  /**
   * 새 게시글을 저장하고 그 뒤에 따라붙는 처리(태그, 미디어, 참조, 이벤트)를 수행한다. Tiptap 본문 경로와 Markdown 초안 경로가 공유하는 부분이며, 두
   * 경로의 차이는 여기 도달하기 전까지의 본문 구성뿐이다.
   */
  private Post persistNewPost(
      Post post,
      @Nullable Project project,
      List<String> tags,
      List<String> projectTags,
      @Nullable List<Long> referencedPostIds,
      List<Media> mediaFiles,
      String contentText) {
    tagService.addTagsToPost(post, project, tags, projectTags);

    Post saved = postRepository.save(post);
    contentMediaService.savePostMediaFiles(saved, mediaFiles);
    postReferenceService.replaceReferences(saved, referencedPostIds);

    applyPublishSideEffects(saved, false, contentText);

    return saved;
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'EDIT')")
  public void updatePost(Long postId, UpdatePostRequest request) {
    Post post = requirePost(postId, null);

    applyUpdate(
        post,
        request.title(),
        request.type(),
        request.status(),
        request.content(),
        request.tags(),
        List.of(),
        request.referencedPostIds(),
        request.coverMediaId(),
        request.removeCover());
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'EDIT')")
  public void updateProjectPost(Long postId, String handle, UpdateProjectPostRequest request) {
    Project project = projectDomainService.getProjectByHandle(handle);
    Post post = requirePost(postId, project);

    applyUpdate(
        post,
        request.title(),
        request.type(),
        request.status(),
        request.content(),
        request.tags(),
        request.projectTags(),
        request.referencedPostIds(),
        request.coverMediaId(),
        request.removeCover());
  }

  private Post getPostById(Long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
  }

  /**
   * Loads a post and enforces that it belongs to {@code expectedProject} ({@code null} meaning a
   * global, non-project post) — the same distinction {@code createPost}/{@code createProjectPost}
   * enforce via separate DTOs, so an author can't edit a project post's tags through the global
   * endpoint (which has no project-tag field and would silently strip them) or vice versa.
   */
  private Post requirePost(Long postId, @Nullable Project expectedProject) {
    Post post = getPostById(postId);
    boolean belongsToExpectedProject =
        expectedProject == null
            ? post.getProject() == null
            : post.getProject() != null
                && post.getProject().getId().equals(expectedProject.getId());
    if (!belongsToExpectedProject) {
      throw new PostNotFoundException(postId);
    }
    return post;
  }

  private void applyUpdate(
      Post post,
      String title,
      PostType type,
      PostStatus status,
      PostContentRequest content,
      List<String> tags,
      List<String> projectTags,
      @Nullable List<Long> referencedPostIds,
      @Nullable String coverMediaId,
      @Nullable Boolean removeCover) {
    if (post.isPublished() && status == PostStatus.DRAFT) {
      throw new InvalidPostPublishRequestException("Published posts cannot be reverted to draft.");
    }
    boolean wasPublished = post.isPublished();

    List<Media> mediaFiles =
        contentMediaService.resolveMediaFilesForUpdate(
            post.getAuthor().getId(), post.getId(), content.mediaIds());

    Media coverMedia =
        type == PostType.LONG
            ? resolveUpdatedCover(post.getAuthor().getId(), post, coverMediaId, removeCover)
            : null;

    post.update(title, type, status, content.json(), content.text(), mediaFiles.size());
    post.updateCoverMedia(coverMedia);
    tagService.replaceTags(post, tags, projectTags);
    postReferenceService.replaceReferences(post, referencedPostIds);
    contentMediaService.replaceMediaFiles(post, mediaFiles);

    applyPublishSideEffects(post, wasPublished, content.text());
  }

  private @Nullable Media resolveCover(Long requesterId, @Nullable String coverMediaId) {
    if (coverMediaId == null) {
      return null;
    }
    return mediaDomainService.linkMedia(requesterId, Long.valueOf(coverMediaId));
  }

  private @Nullable Media resolveUpdatedCover(
      Long requesterId, Post post, @Nullable String coverMediaId, @Nullable Boolean removeCover) {
    if (Boolean.TRUE.equals(removeCover)) {
      return null;
    }
    if (coverMediaId != null) {
      return mediaDomainService.linkMedia(requesterId, Long.valueOf(coverMediaId));
    }
    return post.getCoverMedia();
  }

  /**
   * Publishes the events that follow from a post becoming published (activity count) or having its
   * content changed while published and public (summary/embedding refresh), for both creation and
   * update.
   */
  private void applyPublishSideEffects(Post post, boolean wasPublished, String contentText) {
    if (!wasPublished && post.isPublished()) {
      eventPublisher.publishEvent(
          new PostPublishedEvent(
              post.getId(), post.getAuthor().getId(), LocalDate.now(ZoneId.systemDefault())));
    }

    if (post.isPublished() && post.isPublic()) {
      eventPublisher.publishEvent(new PostContentChangedEvent(post.getId(), contentText));
    }
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'EDIT')")
  public void updatePostSummary(Long postId, UpdatePostSummaryRequest request) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    post.updateSummary(request.summary());
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void pinPost(Long postId, String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);
    Post post = requirePost(postId, project);
    if (post.isPinned()) {
      return;
    }
    if (postRepository.countByProjectAndPinnedAtIsNotNull(project)
        >= PostConstants.MAX_PINNED_POSTS_PER_PROJECT) {
      throw new PostPinLimitExceededException();
    }
    post.pin();
  }

  @Transactional
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'EDIT')")
  public void unpinPost(Long postId, String handle) {
    Project project = projectDomainService.getProjectByHandle(handle);
    Post post = requirePost(postId, project);
    post.unpin();
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'DELETE')")
  public void deletePost(Long postId) {
    if (!postRepository.existsById(postId)) {
      throw new PostNotFoundException(postId);
    }

    // post_series_posts 는 FK cascade 로 함께 지워지지만 그 경로는 JPA 를 타지 않아 뒤쪽 편들의
    // position 이 메워지지 않으므로, 게시글을 지우기 전에 편성을 먼저 정리한다.
    seriesPostRepository
        .findByPostId(postId)
        .ifPresent(
            seriesPost -> {
              Long seriesId = seriesPost.getSeries().getId();
              int position = seriesPost.getPosition();
              seriesPostRepository.delete(seriesPost);
              seriesPostRepository.shiftDownAfter(seriesId, position);
            });

    postRepository.deleteById(postId);
  }
}
