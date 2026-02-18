package com.skkil.sync.provider.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.repository.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ProviderPermissionEvaluator implements CustomPermissionEvaluator {

  private final ProviderRepository providerRepository;

  public ProviderPermissionEvaluator(ProviderRepository providerRepository) {
    this.providerRepository = providerRepository;
  }

  @Override
  public String type() {
    return "PROVIDER";
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    Provider provider = providerRepository.findById(targetId).orElse(null);
    if (provider == null) {
      log.debug("Provider with ID {} not found", targetId);
      return false;
    }

    return switch (permission) {
      case READ -> canRead(user, provider);
      case UPDATE -> canUpdate(user, provider);

      default -> {
        log.debug("Unsupported permission operation: {}", permission);
        yield false;
      }
    };
  }

  private boolean canRead(AuthenticatedUser user, Provider provider) {
    if (provider.isVerified()) {
      log.debug("Provider {} is verified", provider.getId());
      return true;
    }

    if (user == null) {
      log.debug("Unauthenticated user trying to access unverified provider {}", provider.getId());
      return false;
    }

    if (user.isAdmin()) {
      log.debug(
          "User {} is admin, granting read access to unverified provider {}",
          user.userId(),
          provider.getId());
      return true;
    }

    if (user.userId().equals(provider.getCreatedBy().getId())) {
      log.debug(
          "User {} is the creator, granting read access to unverified provider {}",
          user.userId(),
          provider.getId());
      return true;
    }

    return false;
  }

  private boolean canUpdate(AuthenticatedUser user, Provider provider) {
    if (user == null) {
      log.debug("Unauthenticated user trying to update provider {}", provider.getId());
      return false;
    }

    if (user.isAdmin()) {
      log.debug(
          "User {} is admin, granting update access to provider {}",
          user.userId(),
          provider.getId());
      return true;
    }

    if (user.userId().equals(provider.getCreatedBy().getId())) {
      log.debug(
          "User {} is the creator, granting update access to provider {}",
          user.userId(),
          provider.getId());
      return true;
    }

    return false;
  }
}
