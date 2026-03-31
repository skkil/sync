package com.skkil.sync.provider.controller.query;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.service.query.ProviderQueryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderQueryController {

  private final ProviderQueryService providerQueryService;

  public ProviderQueryController(ProviderQueryService providerQueryService) {
    this.providerQueryService = providerQueryService;
  }

  @GetMapping("/search/providers")
  @ResponseStatus(HttpStatus.OK)
  public GetProvidersResponse searchProviders(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) String query,
      @RequestParam(required = false) List<ProviderType> types,
      @Validated CursorPaginationRequest pagination) {
    Long requesterId = user != null ? user.userId() : null;
    return providerQueryService.searchProviders(requesterId, query, types, pagination);
  }

  @GetMapping("/providers/my")
  @ResponseStatus(HttpStatus.OK)
  public GetProvidersResponse getMyProviders(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return providerQueryService.getMyProviders(user.userId(), pagination);
  }
}
