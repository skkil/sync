package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.model.Provider;

interface ProviderStrategy {

  ProviderType getProviderType();

  Provider createProvider(CreateProviderRequest request);

  GetProviderResponse toGetProviderResponse(Provider provider);

  void updateProvider(Provider provider, UpdateProviderRequest request);
}
