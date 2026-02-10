package com.skkil.sync.common.security;

import com.skkil.sync.auth.AuthenticatedUser;

public interface CustomPermissionEvaluator {

  String type();

  boolean hasPermission(AuthenticatedUser user, Long targetId, PermissionOperation permission);
}
