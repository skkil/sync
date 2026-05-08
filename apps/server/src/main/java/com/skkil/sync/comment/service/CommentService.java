package com.skkil.sync.comment.service;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.enums.CommentTargetType;
import com.skkil.sync.comment.exception.CommentNotFoundException;
import com.skkil.sync.comment.exception.InvalidCommentException;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.provider.project.model.TeamBuildingPost;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReflectionRepository reflectionRepository;
  private final TeamBuildingPostRepository teamBuildingPostRepository;
  private final UserDomainService userDomainService;

  public CommentService(
      CommentRepository commentRepository,
      ReflectionRepository reflectionRepository,
      TeamBuildingPostRepository teamBuildingPostRepository,
      UserDomainService userDomainService) {
    this.commentRepository = commentRepository;
    this.reflectionRepository = reflectionRepository;
    this.teamBuildingPostRepository = teamBuildingPostRepository;
    this.userDomainService = userDomainService;
  }

  @Transactional(readOnly = true)
  public GetCommentsResponse getComments(CommentTargetType targetType, Long targetId) {
    List<Comment> roots =
        commentRepository.findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtAscIdAsc(
            targetType, targetId);
    Map<Long, List<Comment>> repliesByParentId = getRepliesByParentId(roots);

    List<GetCommentsResponse.Comment> comments =
        roots.stream().map(comment -> toResponse(comment, repliesByParentId)).toList();

    return new GetCommentsResponse(comments);
  }

  @Transactional
  public CreateCommentResponse createComment(Long authorId, CreateCommentRequest request) {
    validateTargetExists(request.targetType(), request.targetId());

    Comment parent = null;
    if (request.parentId() != null) {
      parent =
          commentRepository
              .findById(request.parentId())
              .orElseThrow(() -> new CommentNotFoundException(request.parentId()));
      validateParent(parent, request);
    }

    User author = userDomainService.getUserReference(authorId);
    Comment comment =
        Comment.builder()
            .author(author)
            .targetType(request.targetType())
            .targetId(request.targetId())
            .parent(parent)
            .content(request.content())
            .build();

    comment = commentRepository.save(comment);
    incrementTargetCommentCount(request.targetType(), request.targetId());

    return new CreateCommentResponse(comment.getId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#commentId, 'COMMENT', 'EDIT')")
  public void updateComment(Long commentId, UpdateCommentRequest request) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));
    if (comment.isDeleted()) {
      throw new InvalidCommentException("Deleted comment cannot be updated.");
    }

    comment.updateContent(request.content());
  }

  @Transactional
  @PreAuthorize("hasPermission(#commentId, 'COMMENT', 'DELETE')")
  public void deleteComment(Long commentId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));
    if (!comment.isDeleted()) {
      comment.delete();
      decrementTargetCommentCount(comment.getTargetType(), comment.getTargetId());
    }
  }

  private void validateTargetExists(CommentTargetType targetType, Long targetId) {
    boolean exists =
        switch (targetType) {
          case REFLECTION -> reflectionRepository.existsById(targetId);
          case TEAM_BUILDING_POST -> teamBuildingPostRepository.existsById(targetId);
        };

    if (!exists) {
      throw new InvalidCommentException("Comment target does not exist.");
    }
  }

  private void validateParent(Comment parent, CreateCommentRequest request) {
    if (parent.isReply()) {
      throw new InvalidCommentException("Replies cannot have replies.");
    }
    if (!parent.getTargetType().equals(request.targetType())
        || !parent.getTargetId().equals(request.targetId())) {
      throw new InvalidCommentException("Reply target must match parent comment target.");
    }
  }

  private Map<Long, List<Comment>> getRepliesByParentId(List<Comment> roots) {
    if (roots.isEmpty()) {
      return Map.of();
    }

    return commentRepository.findByParentInOrderByCreatedAtAscIdAsc(roots).stream()
        .collect(Collectors.groupingBy(comment -> comment.getParent().getId()));
  }

  private GetCommentsResponse.Comment toResponse(
      Comment comment, Map<Long, List<Comment>> repliesByParentId) {
    List<GetCommentsResponse.Comment> replies =
        repliesByParentId.getOrDefault(comment.getId(), List.of()).stream()
            .map(reply -> toResponse(reply, Map.of()))
            .toList();

    return new GetCommentsResponse.Comment(
        comment.getId(),
        new GetCommentsResponse.Author(
            comment.getAuthor().getId(), comment.getAuthor().getHandle()),
        comment.isDeleted() ? null : comment.getContent(),
        comment.isDeleted(),
        comment.getLikeCount(),
        comment.getCreatedAt(),
        comment.getUpdatedAt(),
        new ArrayList<>(replies));
  }

  private void incrementTargetCommentCount(CommentTargetType targetType, Long targetId) {
    switch (targetType) {
      case REFLECTION -> {
        Reflection reflection =
            reflectionRepository
                .findById(targetId)
                .orElseThrow(() -> new InvalidCommentException("Comment target does not exist."));
        reflection.incrementCommentCount();
      }
      case TEAM_BUILDING_POST -> {
        TeamBuildingPost post =
            teamBuildingPostRepository
                .findById(targetId)
                .orElseThrow(() -> new InvalidCommentException("Comment target does not exist."));
        post.incrementCommentCount();
      }
    }
  }

  private void decrementTargetCommentCount(CommentTargetType targetType, Long targetId) {
    switch (targetType) {
      case REFLECTION ->
          reflectionRepository.findById(targetId).ifPresent(Reflection::decrementCommentCount);
      case TEAM_BUILDING_POST ->
          teamBuildingPostRepository
              .findById(targetId)
              .ifPresent(TeamBuildingPost::decrementCommentCount);
    }
  }
}
