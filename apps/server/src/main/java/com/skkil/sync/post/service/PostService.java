package com.skkil.sync.post.service;

import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostSummaryRequest;
import com.skkil.sync.post.dto.response.CreatePostResponse;
import com.skkil.sync.post.event.PostCreatedEvent;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostMediaFile;
import com.skkil.sync.post.model.PostSummary;
import com.skkil.sync.post.repository.PostLikeRepository;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.repository.PostSummaryRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.service.ProjectDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.LocalDate;
import java.time.ZoneId;
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
  private final PostSummaryRepository postSummaryRepository;
  private final PostLikeRepository postLikeRepository;

  public PostService(
      UserDomainService userDomainService,
      ProjectDomainService projectDomainService,
      TagService tagService,
      PostContentMediaService contentMediaService,
      PostRepository postRepository,
      PostMediaFileRepository postMediaFileRepository,
      PostSummaryRepository postSummaryRepository,
      PostLikeRepository postLikeRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userDomainService = userDomainService;
    this.projectDomainService = projectDomainService;
    this.tagService = tagService;
    this.contentMediaService = contentMediaService;
    this.postRepository = postRepository;
    this.postMediaFileRepository = postMediaFileRepository;
    this.postSummaryRepository = postSummaryRepository;
    this.postLikeRepository = postLikeRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public CreatePostResponse createPost(Long authorId, CreatePostRequest request) {
    User author = userDomainService.getUserReference(authorId);

    String slug =
        request.title() == null
            ? String.format("%s-%d", author.getHandle(), System.currentTimeMillis())
            : Slugify.slugify(request.title());

    PostContentMediaService.PreparedContent preparedContent =
        contentMediaService.prepareContentForCreate(authorId, request.content().json());

    Post.PostBuilder postBuilder =
        Post.builder()
            .slug(slug)
            .author(author)
            .title(request.title())
            .type(request.type())
            .content(preparedContent.content());

    if (request.projectId() != null) {
      Project project = projectDomainService.getProject(request.projectId());
      postBuilder.project(project);
    }

    Post post = postBuilder.build();
    tagService.addTagsToPost(post, request.tags());

    post = postRepository.save(post);

    for (int i = 0; i < preparedContent.mediaFiles().size(); i++) {
      postMediaFileRepository.save(new PostMediaFile(post, preparedContent.mediaFiles().get(i), i));
    }

    postRepository.incrementActivityCount(
        author.getId(), LocalDate.ofInstant(post.getCreatedAt(), ZoneId.systemDefault()));

    eventPublisher.publishEvent(new PostCreatedEvent(post.getId(), request.content().text()));

    return new CreatePostResponse(post.getSlug());
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'EDIT')")
  public void updatePost(Long postId, UpdatePostRequest request) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    post.updateContent(request.content());

    if (request.projectId() == null) {
      post.unlinkProject();
    } else {
      Project project = projectDomainService.getProject(request.projectId());
      post.linkProject(project);
    }
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'EDIT')")
  public void updatePostSummary(Long postId, UpdatePostSummaryRequest request) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    PostSummary summary =
        postSummaryRepository
            .findById(postId)
            .orElseGet(() -> new PostSummary(post, request.summary()));

    summary.updateSummary(request.summary());
    postSummaryRepository.save(summary);
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'READ')")
  public void likePost(Long userId, Long postId) {
    postLikeRepository.insertAndIncrementIfAbsent(userId, postId);
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'READ')")
  public void unlikePost(Long userId, Long postId) {
    postLikeRepository.deleteAndDecrementIfPresent(userId, postId);
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
