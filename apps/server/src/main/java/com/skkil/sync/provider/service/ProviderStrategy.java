package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.model.Provider;

public interface ProviderStrategy {

  ProviderType getProviderType();

  Provider createProvider(CreateProviderRequest request);

  GetProviderResponse toGetProviderResponse(Provider provider, boolean isMaintainer);

  void updateProvider(Provider provider, UpdateProviderRequest request);
}
