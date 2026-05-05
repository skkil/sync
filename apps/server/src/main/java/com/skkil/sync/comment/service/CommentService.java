package com.skkil.sync.comment.service;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.exception.CommentNotFoundException;
import com.skkil.sync.comment.exception.InvalidCommentException;
import com.skkil.sync.comment.mapper.CommentMapper;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.service.ReflectionDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReflectionDomainService reflectionDomainService;
  private final UserDomainService userDomainService;
  private final MediaDomainService mediaDomainService;
  private final CommentMapper commentMapper;

  public CommentService(
      CommentRepository commentRepository,
      ReflectionDomainService reflectionDomainService,
      UserDomainService userDomainService,
      MediaDomainService mediaDomainService,
      CommentMapper commentMapper) {
    this.commentRepository = commentRepository;
    this.reflectionDomainService = reflectionDomainService;
    this.userDomainService = userDomainService;
    this.mediaDomainService = mediaDomainService;
    this.commentMapper = commentMapper;
  }

  @Transactional(readOnly = true)
  public GetCommentsResponse getReflectionComments(String slug) {
    Reflection reflection = reflectionDomainService.getReflectionBySlug(slug);

    List<Comment> comments = commentRepository.findByReflection(reflection);

    List<Comment> roots = new ArrayList<>();
    Map<Long, List<Comment>> replies = new HashMap<>();
    Map<Long, String> profileImageUrls = new HashMap<>();

    for (Comment comment : comments) {
      if (comment.isReply()) {
        replies.computeIfAbsent(comment.getParent().getId(), k -> new ArrayList<>()).add(comment);
      } else {
        roots.add(comment);
      }

      Long authorId = comment.getAuthor().getId();
      if (!profileImageUrls.containsKey(authorId)) {
        String profileImageUrl =
            mediaDomainService
                .generatePublicGetUrl(comment.getAuthor().getProfileImage())
                .toExternalForm();
        profileImageUrls.put(authorId, profileImageUrl);
      }
    }

    return commentMapper.toGetCommentsResponse(reflection, roots, replies, profileImageUrls);
  }

  @Transactional
  public CreateCommentResponse createComment(
      Long authorId, Long reflectionId, CreateCommentRequest request) {
    User author = userDomainService.getUserReference(authorId);
    Reflection reflection = reflectionDomainService.getReflection(reflectionId);

    Comment parent = null;
    if (request.parentId() != null) {
      parent =
          commentRepository
              .findById(request.parentId())
              .orElseThrow(() -> new CommentNotFoundException(request.parentId()));

      if (parent.isReply()) {
        throw new InvalidCommentException("Replies cannot have replies.");
      }

      if (!reflectionId.equals(parent.getReflection().getId())) {
        throw new InvalidCommentException("Reply target must match parent comment target.");
      }
    }

    Comment comment =
        Comment.builder()
            .author(author)
            .reflection(reflection)
            .parent(parent)
            .content(request.content())
            .build();

    comment = commentRepository.save(comment);
    return new CreateCommentResponse(comment.getId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#commentId, 'COMMENT', 'EDIT')")
  public void updateComment(Long commentId, UpdateCommentRequest request) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));

    comment.updateContent(request.content());
  }

  @Transactional
  @PreAuthorize("hasPermission(#commentId, 'COMMENT', 'DELETE')")
  public void deleteComment(Long commentId) {
    commentRepository
        .findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId))
        .delete();
    ;
  }
}
