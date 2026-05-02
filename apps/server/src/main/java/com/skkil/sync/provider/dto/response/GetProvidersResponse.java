package com.skkil.sync.provider.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.provider.constant.ProviderType;
import lombok.Builder;

public record GetProvidersResponse(CursorPaginationResponse<Provider> providers) {

  @Builder
  public static record Provider(
      ProviderType type, String id, String name, boolean isVerified, boolean isMaintainer) {}
}
