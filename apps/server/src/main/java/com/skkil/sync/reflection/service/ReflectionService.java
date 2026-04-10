package com.skkil.sync.reflection.service;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.model.ProjectExperience;
import com.skkil.sync.experience.service.domain.ExperienceDomainService;
import com.skkil.sync.reflection.dto.request.CreateReflectionRequest;
import com.skkil.sync.reflection.dto.request.UpdateReflectionRequest;
import com.skkil.sync.reflection.dto.response.CreateReflectionResponse;
import com.skkil.sync.reflection.event.ReflectionCreatedEvent;
import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionService {

  private final UserDomainService userDomainService;
  private final ExperienceDomainService experienceDomainService;
  private final ApplicationEventPublisher eventPublisher;

  private final ReflectionRepository reflectionRepository;

  public ReflectionService(
      UserDomainService userDomainService,
      ExperienceDomainService experienceDomainService,
      ReflectionRepository reflectionRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userDomainService = userDomainService;
    this.experienceDomainService = experienceDomainService;
    this.reflectionRepository = reflectionRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public CreateReflectionResponse createReflection(Long authorId, CreateReflectionRequest request) {
    User author = userDomainService.getUserReference(authorId);

    Reflection reflection = Reflection.builder().author(author).content(request.content()).build();

    if (request.experienceId() != null) {
      Experience experience =
          experienceDomainService.getExperience(
              request.experienceId(), ExperienceType.PROJECT_EXPERIENCE);

      reflection.updateExperience((ProjectExperience) experience);
    }

    reflection = reflectionRepository.save(reflection);
    eventPublisher.publishEvent(
        new ReflectionCreatedEvent(reflection.getId(), reflection.getContent()));

    return new CreateReflectionResponse(reflection.getId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#reflectionId, 'REFLECTION', 'EDIT')")
  public void updateReflection(Long reflectionId, UpdateReflectionRequest request) {
    Reflection reflection =
        reflectionRepository
            .findById(reflectionId)
            .orElseThrow(() -> new ReflectionNotFoundException(reflectionId));

    reflection.updateContent(request.content());

    if (request.experienceId() == null) {
      reflection.updateExperience(null);
    } else {
      Experience experience =
          experienceDomainService.getExperience(
              request.experienceId(), ExperienceType.PROJECT_EXPERIENCE);

      reflection.updateExperience((ProjectExperience) experience);
    }
  }

  @Transactional
  @PreAuthorize("hasPermission(#reflectionId, 'REFLECTION', 'DELETE')")
  public void deleteReflection(Long reflectionId) {
    if (!reflectionRepository.existsById(reflectionId)) {
      throw new ReflectionNotFoundException(reflectionId);
    }

    reflectionRepository.deleteById(reflectionId);
  }
}
