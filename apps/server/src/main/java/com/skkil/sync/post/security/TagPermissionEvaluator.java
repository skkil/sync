package com.skkil.sync.post.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.post.exception.TagNotFoundException;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import org.springframework.stereotype.Component;

@Component
public class TagPermissionEvaluator implements CustomPermissionEvaluator<Long> {

  private final TagRepository tagRepository;
  private final TeammateRepository teammateRepository;

  public TagPermissionEvaluator(
      TagRepository tagRepository, TeammateRepository teammateRepository) {
    this.tagRepository = tagRepository;
    this.teammateRepository = teammateRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.TAG;
  }

  @Override
  public boolean hasPermission(AuthenticatedUser user, Long tagId, PermissionOperation permission) {
    Tag tag =
        tagRepository.findByIdWithProject(tagId).orElseThrow(() -> new TagNotFoundException(tagId));

    if (permission == PermissionOperation.READ && tag.getProject() == null) {
      return true;
    }

    if (permission == PermissionOperation.READ
        && tag.getProject() != null
        && tag.getProject().isPublic()) {
      return true;
    }

    if (user == null) {
      return false;
    }

    return switch (permission) {
      case READ, EDIT -> {
        if (tag.getProject() == null) {
          yield permission == PermissionOperation.READ || user.isAdmin();
        }

        yield teammateRepository
            .findByProjectHandleAndUserId(tag.getProject().getHandle(), user.userId())
            .map(teammate -> permission == PermissionOperation.READ || teammate.canManageProject())
            .orElse(false);
      }
      case CREATE, DELETE -> false;
    };
  }
}
