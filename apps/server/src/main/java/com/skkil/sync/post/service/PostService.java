package com.skkil.sync.post.service;

import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostSummaryRequest;
import com.skkil.sync.post.dto.response.CreatePostResponse;
import com.skkil.sync.post.event.PostCreatedEvent;
import com.skkil.sync.post.exception.InvalidPostPublishRequestException;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostMediaFile;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
  private final PostMediaFileRepository postMediaFileRepository;

  public PostService(
      UserDomainService userDomainService,
      ProjectDomainService projectDomainService,
      TagService tagService,
      PostContentMediaService contentMediaService,
      PostRepository postRepository,
      PostMediaFileRepository postMediaFileRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userDomainService = userDomainService;
    this.projectDomainService = projectDomainService;
    this.tagService = tagService;
    this.contentMediaService = contentMediaService;
    this.postRepository = postRepository;
    this.postMediaFileRepository = postMediaFileRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public CreatePostResponse createPost(Long authorId, CreatePostRequest request) {
    PostStatus status = resolveStatus(request);
    validateCreatePostRequest(request, status);

    User author = userDomainService.getUserReference(authorId);

    String slug = createSlug(author, request);

    List<Media> mediaFiles =
        contentMediaService.resolveMediaFilesForCreate(authorId, request.content().mediaIds());

    Post.PostBuilder postBuilder =
        Post.builder()
            .slug(slug)
            .author(author)
            .type(request.type())
            .status(status)
            .title(request.title())
            .content(request.content().json());

    Project project = null;
    if (request.project() != null) {
      project = projectDomainService.getProjectByHandle(request.project().handle());
      postBuilder.project(project);
    }

    Post post = postBuilder.build();
    tagService.addTagsToPost(post, project, request.tags());

    post = postRepository.save(post);

    for (int i = 0; i < mediaFiles.size(); i++) {
      postMediaFileRepository.save(new PostMediaFile(post, mediaFiles.get(i), i));
    }

    if (post.isPublished()) {
      postRepository.incrementActivityCount(
          author.getId(), LocalDate.ofInstant(post.getCreatedAt(), ZoneId.systemDefault()));
    }

    if (post.isPublished() && post.isPublic()) {
      eventPublisher.publishEvent(new PostCreatedEvent(post.getId(), request.content().text()));
    }

    return new CreatePostResponse(post.getSlug());
  }

  private static PostStatus resolveStatus(CreatePostRequest request) {
    return request.status() == null ? PostStatus.PUBLISHED : request.status();
  }

  private static void validateCreatePostRequest(CreatePostRequest request, PostStatus status) {
    if (status != PostStatus.PUBLISHED) {
      return;
    }

    if (requiresTitle(request.type()) && isBlank(request.title())) {
      throw new InvalidPostPublishRequestException(
          "Published article and question posts require a title.");
    }

    if (!hasPublishableTags(request.tags())) {
      throw new InvalidPostPublishRequestException("Published posts require at least one tag.");
    }
  }

  private static boolean requiresTitle(PostType type) {
    return type != PostType.SHORT;
  }

  private static boolean hasPublishableTags(List<String> tags) {
    return tags != null && tags.stream().anyMatch(tag -> tag != null && !tag.isBlank());
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private static String createSlug(User author, CreatePostRequest request) {
    if (isBlank(request.title())) {
      return String.format("%s-%d", author.getHandle(), System.currentTimeMillis());
    }

    return Slugify.slugify(request.title());
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'EDIT')")
  public void updatePost(Long postId, UpdatePostRequest request) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    post.updateContent(request.content());
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
