package com.skkil.sync.user.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ProfilePermissionEvaluator implements CustomPermissionEvaluator {

  private final UserRepository userRepository;

  public ProfilePermissionEvaluator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.PROFILE;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    User target = userRepository.findById(targetId).orElse(null);
    if (target == null) {
      return false;
    }

    if (!target.isEnabled()) {
      return user.userId().equals(targetId);
    }

    return true;
  }
}
