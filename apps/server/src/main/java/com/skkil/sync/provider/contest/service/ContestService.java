package com.skkil.sync.provider.contest.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.contest.dto.request.CreateContestRequest;
import com.skkil.sync.provider.contest.dto.request.UpdateContestRequest;
import com.skkil.sync.provider.contest.dto.response.GetContestResponse;
import com.skkil.sync.provider.contest.model.Contest;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.repository.ProviderRepository;
import com.skkil.sync.provider.service.ProviderStrategy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContestService implements ProviderStrategy {

  private final ProviderRepository providerRepository;

  public ContestService(ProviderRepository providerRepository) {
    this.providerRepository = providerRepository;
  }

  @Override
  public ProviderType getProviderType() {
    return ProviderType.CONTEST;
  }

  @Override
  public Provider createProvider(CreateProviderRequest request) {
    CreateContestRequest contestRequest = (CreateContestRequest) request;

    Contest contest =
        Contest.builder().name(request.name()).description(request.description()).build();

    if (contestRequest.hostProviderId() != null) {
      contest.setHostProvider(providerRepository.getReferenceById(contestRequest.hostProviderId()));
    }

    return contest;
  }

  @Override
  public GetProviderResponse toGetProviderResponse(Provider provider, boolean isMaintainer) {
    Contest contest = (Contest) provider;

    var hostProvider =
        contest.getHostProvider() == null
            ? null
            : new GetContestResponse.Provider(
                contest.getHostProvider().getId(),
                contest.getHostProvider().getType(),
                contest.getHostProvider().getName());

    return GetContestResponse.builder()
        .id(provider.getId())
        .type(provider.getType())
        .name(provider.getName())
        .description(provider.getDescription())
        .verifiedBy(provider.getVerifiedBy() != null ? provider.getVerifiedBy().getId() : null)
        .hostProvider(hostProvider)
        .contactInfo(provider.getContactInfo())
        .createdAt(LocalDateTime.ofInstant(provider.getCreatedAt(), ZoneId.systemDefault()))
        .updatedAt(LocalDateTime.ofInstant(provider.getUpdatedAt(), ZoneId.systemDefault()))
        .isMaintainer(isMaintainer)
        .build();
  }

  @Override
  @Transactional
  public void updateProvider(Provider provider, UpdateProviderRequest request) {
    UpdateContestRequest contestRequest = (UpdateContestRequest) request;

    if (contestRequest.name() != null) {
      provider.setName(contestRequest.name());
    }

    if (contestRequest.description() != null) {
      provider.setDescription(contestRequest.description());
    }
  }
}
