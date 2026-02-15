package com.skkil.sync.provider.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import java.util.List;

public record GetProvidersResponse(List<Provider> providers) {

  public static record Provider(ProviderType type, String id, String name) {}
}
