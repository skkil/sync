package com.skkil.sync.provider.dto.response;

import org.springframework.data.domain.Page;

public record GetUnverifiedProvidersResponse(Page<Provider> providers) {

  public static record Provider(Long id, String name) {}
}
