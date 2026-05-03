package com.skkil.sync.comment.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentPermissionEvaluator implements CustomPermissionEvaluator {

  private final CommentRepository commentRepository;

  public CommentPermissionEvaluator(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.COMMENT;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    Comment comment = commentRepository.findById(targetId).orElse(null);
    if (comment == null) {
      log.debug("Comment with ID {} not found", targetId);
      return false;
    }

    return switch (permission) {
      case EDIT, DELETE -> {
        if (user == null) {
          log.debug("Unauthenticated user cannot change comment {}", targetId);
          yield false;
        }

        if (comment.isDeleted()) {
          log.debug("Cannot edit or delete already deleted comment {}", targetId);
          yield false;
        }

        yield user.userId().equals(comment.getAuthor().getId());
      }
      default -> false;
    };
  }
}
