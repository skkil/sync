package com.skkil.sync.common.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;

public interface CustomPermissionEvaluator {

  PermissionEvaluatorType type();

  boolean hasPermission(AuthenticatedUser user, Long targetId, PermissionOperation permission);
}
