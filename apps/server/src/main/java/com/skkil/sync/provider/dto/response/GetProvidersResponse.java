package com.skkil.sync.provider.dto.response;

import java.util.List;

public record GetProvidersResponse(List<Provider> providers) {

  public static record Provider(String id, String name) {}
}
