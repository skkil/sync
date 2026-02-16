package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateContestOccurrenceRequest;
import com.skkil.sync.provider.dto.request.CreateContestRequest;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateContestRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetContestResponse;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.model.Contest;
import com.skkil.sync.provider.model.ContestOccurrence;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.repository.ContestOccurrenceRepository;
import com.skkil.sync.provider.repository.ContestRepository;
import com.skkil.sync.provider.repository.ProviderRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContestService implements ProviderStrategy {

  private final ProviderRepository providerRepository;
  private final ContestRepository contestRepository;
  private final ContestOccurrenceRepository contestOccurrenceRepository;

  public ContestService(
      ProviderRepository providerRepository,
      ContestRepository contestRepository,
      ContestOccurrenceRepository contestOccurrenceRepository) {
    this.providerRepository = providerRepository;
    this.contestRepository = contestRepository;
    this.contestOccurrenceRepository = contestOccurrenceRepository;
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
  public GetProviderResponse toGetProviderResponse(Provider provider) {
    Contest contest = (Contest) provider;

    var hostProvider =
        new GetContestResponse.Provider(
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

  @Transactional
  public void createContestOccurrence(Long contestId, CreateContestOccurrenceRequest request) {
    Contest contest = contestRepository.getReferenceById(contestId);

    ContestOccurrence occurrence =
        ContestOccurrence.builder()
            .contest(contest)
            .title(request.title())
            .description(request.description())
            .build();
    contestOccurrenceRepository.save(occurrence);
  }
}
