package com.skkil.sync.post.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostPermissionEvaluator implements CustomPermissionEvaluator {

  private PostRepository postRepository;

  public PostPermissionEvaluator(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.POST;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    Post post = postRepository.findById(targetId).orElse(null);
    if (post == null) {
      log.debug("Post with ID {} not found", targetId);
      return false;
    }

    return switch (permission) {
      case EDIT -> canEdit(user, post);
      case DELETE -> canDelete(user, post);

      default -> {
        log.debug("Unsupported permission operation: {}", permission);
        yield false;
      }
    };
  }

  private boolean canEdit(AuthenticatedUser user, Post post) {
    if (user == null) {
      log.debug("Unauthenticated user cannot edit post");
      return false;
    }

    boolean isOwner = user.userId().equals(post.getAuthor().getId());
    if (!isOwner) {
      log.debug("User {} is not the owner of post {}, cannot edit", user.userId(), post.getId());
      return false;
    }

    return true;
  }

  private boolean canDelete(AuthenticatedUser user, Post post) {
    if (user == null) {
      log.debug("Unauthenticated user cannot delete post");
      return false;
    }

    boolean isOwner = user.userId().equals(post.getAuthor().getId());
    if (!isOwner) {
      log.debug("User {} is not the owner of post {}, cannot delete", user.userId(), post.getId());
      return false;
    }

    return true;
  }
}
