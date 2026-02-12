package com.skkil.sync.experience.service;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.dto.request.CreateExperienceRequest;
import com.skkil.sync.experience.dto.request.UpdateExperienceRequest;
import com.skkil.sync.experience.dto.response.CreateExperienceResponse;
import com.skkil.sync.experience.dto.response.GetExperiencesResponse;
import com.skkil.sync.experience.exception.ExperienceNotFoundException;
import com.skkil.sync.experience.exception.ProviderNotVerifiedException;
import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.repository.ExperienceRepository;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.service.ProviderService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceService {

  private final UserService userService;
  private final ProviderService providerService;
  private final ExperienceRepository experienceRepository;
  private final Map<ExperienceType, ExperienceStrategy> experienceStrategyMap;

  public ExperienceService(
      UserService userService,
      ProviderService providerService,
      ExperienceRepository experienceRepository,
      List<ExperienceStrategy> experienceStrategies) {
    this.userService = userService;
    this.providerService = providerService;
    this.experienceRepository = experienceRepository;
    this.experienceStrategyMap =
        experienceStrategies.stream()
            .collect(Collectors.toMap(ExperienceStrategy::getExperienceType, Function.identity()));
  }

  @Transactional
  public CreateExperienceResponse createExperience(Long userId, CreateExperienceRequest request) {
    Provider provider = providerService.getProviderEntity(request.providerId());
    if (!request.type().getProviderType().equals(provider.getType())) {
      throw new IllegalArgumentException("Provider type does not match experience type");
    }

    if (!provider.isVerified()) {
      throw new ProviderNotVerifiedException(provider.getId());
    }

    ExperienceStrategy experienceStrategy = getExperienceStrategy(request.type());

    Experience experience = experienceStrategy.createExperience(request);
    experience.setProvider(provider);

    User user = userService.getUserReference(userId);
    experience.setUser(user);

    experience.setStartDate(request.startDate());
    experience.setEndDate(request.endDate());

    experience = experienceRepository.save(experience);

    return new CreateExperienceResponse(experience.getId());
  }

  @Transactional(readOnly = true)
  public GetExperiencesResponse getExperiences(Long userId, Long requesterId) {
    boolean isOwner = requesterId != null && requesterId.equals(userId);

    List<Experience> experiences =
        isOwner
            ? experienceRepository.findByUserWithProvider(userId)
            : experienceRepository.findByUserAndPublicWithProvider(userId);

    var experienceResponse =
        experiences.stream()
            .map(
                experience -> {
                  var provider =
                      new GetExperiencesResponse.Provider(
                          experience.getProvider().getId(), experience.getProvider().getName());

                  return new GetExperiencesResponse.Experience(
                      experience.getType(),
                      experience.getVisibility(),
                      provider,
                      experience.getId(),
                      experience.getStartDate(),
                      experience.getEndDate());
                });

    return new GetExperiencesResponse(experienceResponse.toList());
  }

  @Transactional
  public void updateExperience(Long userId, Long experienceId, UpdateExperienceRequest request) {
    Experience experience =
        experienceRepository
            .findById(experienceId)
            .orElseThrow(() -> new ExperienceNotFoundException(experienceId));

    if (!userId.equals(experience.getUser().getId())) {
      throw new ExperienceNotFoundException(experienceId);
    }

    if (request.visibility() != null) {
      experience.setVisibility(request.visibility());
    }
  }

  @Transactional
  public void deleteExperience(Long id) {
    experienceRepository.deleteById(id);
  }

  private ExperienceStrategy getExperienceStrategy(ExperienceType type) {
    ExperienceStrategy strategy = experienceStrategyMap.get(type);
    if (strategy == null) {
      throw new IllegalArgumentException("Unsupported experience type: " + type);
    }

    return strategy;
  }
}
