package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.CreateProviderResponse;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import com.skkil.sync.provider.dto.response.GetUnverifiedProvidersResponse;
import com.skkil.sync.provider.exception.ProviderNotFoundException;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.repository.ProviderRepository;
import com.skkil.sync.user.model.User;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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

  @Transactional
  public CreateProviderResponse createProvider(Long userId, CreateProviderRequest request) {
    ProviderStrategy providerStrategy = getProviderStrategy(request.type());

    Provider provider = providerStrategy.createProvider(request);
    provider.setCreatedBy(new User(userId));

    providerRepository.save(provider);

    return new CreateProviderResponse(provider.getId());
  }

  @Transactional(readOnly = true)
  public GetProvidersResponse getProviders(ProviderType type) {
    List<Provider> providers = providerRepository.findByTypeAndVerified(type);

    return new GetProvidersResponse(
        providers.stream()
            .map(
                provider ->
                    new GetProvidersResponse.Provider(
                        provider.getId().toString(), provider.getName()))
            .toList());
  }

  @Transactional(readOnly = true)
  public GetUnverifiedProvidersResponse getUnverifiedProviders(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    log.debug("Fetching unverified providers, page: {}, size: {}", page, size);
    return new GetUnverifiedProvidersResponse(
        providerRepository
            .findUnverifiedProviders(pageRequest)
            .map(
                provider ->
                    new GetUnverifiedProvidersResponse.Provider(
                        provider.getId(), provider.getName())));
  }

  @Transactional(readOnly = true)
  public Provider getProviderEntity(Long id) {
    return providerRepository.findById(id).orElseThrow(() -> new ProviderNotFoundException(id));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#id, 'PROVIDER', 'READ')")
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

  @Transactional
  public void verifyProvider(Long userId, Long id) {
    Provider provider =
        providerRepository.findById(id).orElseThrow(() -> new ProviderNotFoundException(id));

    provider.setVerifiedBy(new User(userId));
  }

  @Transactional
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
