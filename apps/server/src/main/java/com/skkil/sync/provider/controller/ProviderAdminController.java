package com.skkil.sync.provider.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.provider.service.ProviderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderAdminController {

  private final ProviderService providerService;

  public ProviderAdminController(ProviderService providerService) {
    this.providerService = providerService;
  }

  @PatchMapping("/admin/providers/{id}/verify")
  public void verifyProvider(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
    providerService.verifyProvider(user.userId(), id);
  }
}
