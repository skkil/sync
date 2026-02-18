package com.skkil.sync.provider.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import lombok.Builder;
import org.springframework.data.domain.Page;

public record GetProvidersResponse(Page<Provider> providers) {

  @Builder
  public static record Provider(ProviderType type, String id, String name) {}
}
