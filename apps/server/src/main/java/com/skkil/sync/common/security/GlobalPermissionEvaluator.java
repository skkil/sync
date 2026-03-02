package com.skkil.sync.common.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GlobalPermissionEvaluator implements PermissionEvaluator {

  private Map<PermissionEvaluatorType, CustomPermissionEvaluator> evaluators;

  public GlobalPermissionEvaluator(List<CustomPermissionEvaluator> evaluators) {
    this.evaluators =
        evaluators.stream()
            .collect(Collectors.toMap(CustomPermissionEvaluator::type, Function.identity()));
    log.debug("Registered permission evaluators: {}", this.evaluators.keySet());
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Object targetDomainObject, Object permission) {
    log.debug("hasPermission with targetDomainObject is not supported");
    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Serializable targetId, String targetType, Object permission) {
    if (targetId == null) {
      log.debug("Target ID is null");
      return false;
    }

    PermissionEvaluatorType type = PermissionEvaluatorType.fromString(targetType);
    var evaluator = evaluators.get(type);
    if (evaluator == null) {
      log.debug("No permission evaluator found for target type: {}", targetType);
      return false;
    }

    AuthenticatedUser user = null;
    if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
      user = (AuthenticatedUser) authentication.getPrincipal();
    }

    return evaluator.hasPermission(
        user, (Long) targetId, PermissionOperation.valueOf(permission.toString()));
  }
}
