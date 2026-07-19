package com.skkil.sync.post.service;

import com.skkil.sync.media.model.Media;
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
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostRepository;
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

  private final TagService tagService;
  private final PostContentMediaService contentMediaService;
  private final ApplicationEventPublisher eventPublisher;

  private final PostRepository postRepository;

  public PostService(
      UserDomainService userDomainService,
      ProjectDomainService projectDomainService,
      TagService tagService,
      PostContentMediaService contentMediaService,
      PostRepository postRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userDomainService = userDomainService;
    this.projectDomainService = projectDomainService;
    this.tagService = tagService;
    this.contentMediaService = contentMediaService;
    this.postRepository = postRepository;
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
      @Nullable Project project) {
    status = status == null ? PostStatus.PUBLISHED : status;
    projectTags = projectTags == null ? List.of() : projectTags;

    User author = userDomainService.getUserReference(authorId);
    String slug = PostSlugGenerator.generate(author, title);

    List<Media> mediaFiles =
        contentMediaService.resolveMediaFilesForCreate(authorId, content.mediaIds());

    Post.PostBuilder postBuilder =
        Post.builder()
            .slug(slug)
            .author(author)
            .type(type)
            .status(status)
            .title(title)
            .content(content.json());
    if (project != null) {
      postBuilder.project(project);
    }
    Post post = postBuilder.build();

    post.updateContent(content.json(), content.text(), mediaFiles.size());
    tagService.addTagsToPost(post, project, tags, projectTags);

    post = postRepository.save(post);
    contentMediaService.savePostMediaFiles(post, mediaFiles);

    applyPublishSideEffects(post, false, content.text());

    return new CreatePostResponse(post.getSlug());
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
        List.of());
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
        request.projectTags());
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
      List<String> projectTags) {
    if (post.isPublished() && status == PostStatus.DRAFT) {
      throw new InvalidPostPublishRequestException("Published posts cannot be reverted to draft.");
    }
    boolean wasPublished = post.isPublished();

    List<Media> mediaFiles =
        contentMediaService.resolveMediaFilesForUpdate(
            post.getAuthor().getId(), post.getId(), content.mediaIds());

    post.update(title, type, status, content.json(), content.text(), mediaFiles.size());
    tagService.replaceTags(post, tags, projectTags);
    contentMediaService.replaceMediaFiles(post, mediaFiles);

    applyPublishSideEffects(post, wasPublished, content.text());
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
  @PreAuthorize("hasPermission(#postId, 'POST', 'DELETE')")
  public void deletePost(Long postId) {
    if (!postRepository.existsById(postId)) {
      throw new PostNotFoundException(postId);
    }

    postRepository.deleteById(postId);
  }
}
