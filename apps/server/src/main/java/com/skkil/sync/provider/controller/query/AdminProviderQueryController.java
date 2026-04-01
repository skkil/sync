package com.skkil.sync.provider.controller.query;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.service.query.ProviderQueryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminProviderQueryController {

  private final ProviderQueryService providerQueryService;

  public AdminProviderQueryController(ProviderQueryService providerQueryService) {
    this.providerQueryService = providerQueryService;
  }

  @GetMapping("/admin/providers/unverified")
  public GetProvidersResponse getUnverifiedProviders(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return providerQueryService.getUnverifiedProviders(user.userId(), pagination);
  }
}
