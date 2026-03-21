package com.skkil.sync.experience.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.repository.ExperienceRepository;
import org.springframework.stereotype.Component;

@Component
public class ExperiencePermissionEvaluator implements CustomPermissionEvaluator {

  private final ExperienceRepository experienceRepository;

  public ExperiencePermissionEvaluator(ExperienceRepository experienceRepository) {
    this.experienceRepository = experienceRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.EXPERIENCE;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    Experience experience = experienceRepository.findById(targetId).orElse(null);
    if (experience == null) {
      return false;
    }

    return switch (permission) {
      case READ -> canRead(user, experience);
      case EDIT -> canEdit(user, experience);
      case DELETE -> canDelete(user, experience);

      default -> {
        yield false;
      }
    };
  }

  private boolean canRead(AuthenticatedUser user, Experience experience) {
    if (experience.isPublic()) {
      return true;
    }

    if (user == null) {
      return false;
    }

    return user.userId().equals(experience.getUser().getId());
  }

  private boolean canEdit(AuthenticatedUser user, Experience experience) {
    if (user == null) {
      return false;
    }

    return user.userId().equals(experience.getUser().getId());
  }

  private boolean canDelete(AuthenticatedUser user, Experience experience) {
    if (user == null) {
      return false;
    }

    return user.userId().equals(experience.getUser().getId());
  }
}
