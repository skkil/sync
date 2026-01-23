package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.CreateProviderResponse;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.exception.ProviderNotFoundException;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.repository.ProviderRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderService {

  private final ProviderRepository providerRepository;
  private final Map<ProviderType, ProviderStrategy> providerStrategyMap;

  public ProviderService(
      ProviderRepository providerRepository, List<ProviderStrategy> providerStrategies) {
    this.providerRepository = providerRepository;
    this.providerStrategyMap =
        providerStrategies.stream()
            .collect(Collectors.toMap(ProviderStrategy::getProviderType, Function.identity()));
  }

  public CreateProviderResponse createProvider(CreateProviderRequest request) {
    ProviderStrategy providerStrategy = getProviderStrategy(request.type());
    Provider provider = providerStrategy.createProvider(request);
    return new CreateProviderResponse(provider.getId());
  }

  @Transactional(readOnly = true)
  public GetProviderResponse getProvider(Long id) {
    Provider provider =
        providerRepository.findById(id).orElseThrow(() -> new ProviderNotFoundException(id));

    ProviderStrategy providerStrategy = getProviderStrategy(provider.getType());
    return providerStrategy.toGetProviderResponse(provider);
  }

  @Transactional
  public void updateProvider(Long id, UpdateProviderRequest request) {
    Provider provider =
        providerRepository.findById(id).orElseThrow(() -> new ProviderNotFoundException(id));

    ProviderStrategy providerStrategy = getProviderStrategy(provider.getType());
    providerStrategy.updateProvider(provider, request);
  }

  public void deleteProvider(Long id) {
    providerRepository.deleteById(id);
  }

  private ProviderStrategy getProviderStrategy(ProviderType providerType) {
    ProviderStrategy strategy = providerStrategyMap.get(providerType);
    if (strategy == null) {
      throw new IllegalArgumentException("Unsupported provider type: " + providerType);
    }

    return strategy;
  }
}
