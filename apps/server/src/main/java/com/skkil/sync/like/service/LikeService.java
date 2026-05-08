package com.skkil.sync.like.service;

import com.skkil.sync.comment.exception.CommentNotFoundException;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.like.dto.request.CreateLikeRequest;
import com.skkil.sync.like.dto.response.CreateLikeResponse;
import com.skkil.sync.like.enums.LikeTargetType;
import com.skkil.sync.like.exception.InvalidLikeException;
import com.skkil.sync.like.model.Like;
import com.skkil.sync.like.repository.LikeRepository;
import com.skkil.sync.provider.project.model.TeamBuildingPost;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

  private final LikeRepository likeRepository;
  private final ReflectionRepository reflectionRepository;
  private final TeamBuildingPostRepository teamBuildingPostRepository;
  private final CommentRepository commentRepository;
  private final UserDomainService userDomainService;

  public LikeService(
      LikeRepository likeRepository,
      ReflectionRepository reflectionRepository,
      TeamBuildingPostRepository teamBuildingPostRepository,
      CommentRepository commentRepository,
      UserDomainService userDomainService) {
    this.likeRepository = likeRepository;
    this.reflectionRepository = reflectionRepository;
    this.teamBuildingPostRepository = teamBuildingPostRepository;
    this.commentRepository = commentRepository;
    this.userDomainService = userDomainService;
  }

  @Transactional
  public CreateLikeResponse like(Long userId, CreateLikeRequest request) {
    validateLikableTarget(request.targetType(), request.targetId());

    if (hasLiked(userId, request)) {
      return getLikedResponse(request);
    }

    User user = userDomainService.getUserReference(userId);
    likeRepository.saveAndFlush(
        Like.builder()
            .user(user)
            .targetType(request.targetType())
            .targetId(request.targetId())
            .build());

    Long likeCount = incrementLikeCount(request.targetType(), request.targetId());

    return new CreateLikeResponse(likeCount, true);
  }

  @Transactional(readOnly = true)
  public boolean hasLiked(Long userId, CreateLikeRequest request) {
    return likeRepository.existsByUserIdAndTargetTypeAndTargetId(
        userId, request.targetType(), request.targetId());
  }

  @Transactional(readOnly = true)
  public CreateLikeResponse getLikedResponse(CreateLikeRequest request) {
    return new CreateLikeResponse(getLikeCount(request.targetType(), request.targetId()), true);
  }

  @Transactional
  public CreateLikeResponse unlike(Long userId, CreateLikeRequest request) {
    validateTargetExists(request.targetType(), request.targetId());

    return likeRepository
        .findByUserIdAndTargetTypeAndTargetId(userId, request.targetType(), request.targetId())
        .map(
            like -> {
              likeRepository.delete(like);
              Long likeCount = decrementLikeCount(request.targetType(), request.targetId());
              return new CreateLikeResponse(likeCount, false);
            })
        .orElseGet(
            () ->
                new CreateLikeResponse(
                    getLikeCount(request.targetType(), request.targetId()), false));
  }

  private void validateLikableTarget(LikeTargetType targetType, Long targetId) {
    switch (targetType) {
      case REFLECTION -> getReflection(targetId);
      case TEAM_BUILDING_POST -> getTeamBuildingPost(targetId);
      case COMMENT -> {
        Comment comment = getComment(targetId);
        if (comment.isDeleted()) {
          throw new InvalidLikeException("Deleted comment cannot be liked.");
        }
      }
    }
  }

  private void validateTargetExists(LikeTargetType targetType, Long targetId) {
    switch (targetType) {
      case REFLECTION -> getReflection(targetId);
      case TEAM_BUILDING_POST -> getTeamBuildingPost(targetId);
      case COMMENT -> getComment(targetId);
    }
  }

  private Long getLikeCount(LikeTargetType targetType, Long targetId) {
    return switch (targetType) {
      case REFLECTION -> getReflection(targetId).getLikeCount();
      case TEAM_BUILDING_POST -> getTeamBuildingPost(targetId).getLikeCount();
      case COMMENT -> getComment(targetId).getLikeCount();
    };
  }

  private Long incrementLikeCount(LikeTargetType targetType, Long targetId) {
    return switch (targetType) {
      case REFLECTION -> {
        Reflection reflection = getReflection(targetId);
        reflection.incrementLikeCount();
        yield reflection.getLikeCount();
      }
      case TEAM_BUILDING_POST -> {
        TeamBuildingPost post = getTeamBuildingPost(targetId);
        post.incrementLikeCount();
        yield post.getLikeCount();
      }
      case COMMENT -> {
        Comment comment = getComment(targetId);
        comment.incrementLikeCount();
        yield comment.getLikeCount();
      }
    };
  }

  private Long decrementLikeCount(LikeTargetType targetType, Long targetId) {
    return switch (targetType) {
      case REFLECTION -> {
        Reflection reflection = getReflection(targetId);
        reflection.decrementLikeCount();
        yield reflection.getLikeCount();
      }
      case TEAM_BUILDING_POST -> {
        TeamBuildingPost post = getTeamBuildingPost(targetId);
        post.decrementLikeCount();
        yield post.getLikeCount();
      }
      case COMMENT -> {
        Comment comment = getComment(targetId);
        comment.decrementLikeCount();
        yield comment.getLikeCount();
      }
    };
  }

  private Reflection getReflection(Long targetId) {
    return reflectionRepository
        .findById(targetId)
        .orElseThrow(() -> new ReflectionNotFoundException(targetId));
  }

  private TeamBuildingPost getTeamBuildingPost(Long targetId) {
    return teamBuildingPostRepository
        .findById(targetId)
        .orElseThrow(() -> new InvalidLikeException("Like target does not exist."));
  }

  private Comment getComment(Long targetId) {
    return commentRepository
        .findById(targetId)
        .orElseThrow(() -> new CommentNotFoundException(targetId));
  }
}
