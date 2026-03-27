package com.skkil.sync.reflection.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReflectionPermissionEvaluator implements CustomPermissionEvaluator {

  private ReflectionRepository reflectionRepository;

  public ReflectionPermissionEvaluator(ReflectionRepository reflectionRepository) {
    this.reflectionRepository = reflectionRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.REFLECTION;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    Reflection reflection = reflectionRepository.findById(targetId).orElse(null);
    if (reflection == null) {
      log.debug("Reflection with ID {} not found", targetId);
      return false;
    }

    return switch (permission) {
      case EDIT -> canEdit(user, reflection);
      case DELETE -> canDelete(user, reflection);

      default -> {
        log.debug("Unsupported permission operation: {}", permission);
        yield false;
      }
    };
  }

  private boolean canEdit(AuthenticatedUser user, Reflection reflection) {
    if (user == null) {
      log.debug("Unauthenticated user cannot edit reflection");
      return false;
    }

    boolean isOwner = user.userId().equals(reflection.getAuthor().getId());
    if (!isOwner) {
      log.debug(
          "User {} is not the owner of reflection {}, cannot edit",
          user.userId(),
          reflection.getId());
      return false;
    }

    return true;
  }

  private boolean canDelete(AuthenticatedUser user, Reflection reflection) {
    if (user == null) {
      log.debug("Unauthenticated user cannot delete reflection");
      return false;
    }

    boolean isOwner = user.userId().equals(reflection.getAuthor().getId());
    if (!isOwner) {
      log.debug(
          "User {} is not the owner of reflection {}, cannot delete",
          user.userId(),
          reflection.getId());
      return false;
    }

    return true;
  }
}
