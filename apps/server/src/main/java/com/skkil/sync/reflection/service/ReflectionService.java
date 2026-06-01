package com.skkil.sync.reflection.service;

import com.skkil.sync.badge.service.BadgeCollectionService;
import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.model.ProjectExperience;
import com.skkil.sync.experience.service.domain.ExperienceDomainService;
import com.skkil.sync.reflection.dto.request.CreateReflectionRequest;
import com.skkil.sync.reflection.dto.request.UpdateReflectionRequest;
import com.skkil.sync.reflection.dto.request.UpdateReflectionSummaryRequest;
import com.skkil.sync.reflection.dto.response.CreateReflectionResponse;
import com.skkil.sync.reflection.event.ReflectionCreatedEvent;
import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.model.ReflectionMediaFile;
import com.skkil.sync.reflection.model.ReflectionSummary;
import com.skkil.sync.reflection.model.Tag;
import com.skkil.sync.reflection.repository.ReflectionMediaFileRepository;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.reflection.repository.ReflectionSummaryRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionService {

  private final UserDomainService userDomainService;
  private final BadgeCollectionService badgeCollectionService;
  private final TagService tagService;
  private final ExperienceDomainService experienceDomainService;
  private final ReflectionContentMediaService contentMediaService;
  private final ApplicationEventPublisher eventPublisher;

  private final ReflectionRepository reflectionRepository;
  private final ReflectionMediaFileRepository reflectionMediaFileRepository;
  private final ReflectionSummaryRepository reflectionSummaryRepository;

  public ReflectionService(
      UserDomainService userDomainService,
      BadgeCollectionService badgeCollectionService,
      TagService tagService,
      ExperienceDomainService experienceDomainService,
      ReflectionContentMediaService contentMediaService,
      ReflectionRepository reflectionRepository,
      ReflectionMediaFileRepository reflectionMediaFileRepository,
      ReflectionSummaryRepository reflectionSummaryRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userDomainService = userDomainService;
    this.badgeCollectionService = badgeCollectionService;
    this.tagService = tagService;
    this.experienceDomainService = experienceDomainService;
    this.contentMediaService = contentMediaService;
    this.reflectionRepository = reflectionRepository;
    this.reflectionMediaFileRepository = reflectionMediaFileRepository;
    this.reflectionSummaryRepository = reflectionSummaryRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public CreateReflectionResponse createReflection(Long authorId, CreateReflectionRequest request) {
    User author = userDomainService.getUserReference(authorId);

    String slug =
        request.title() == null
            ? String.format("%s-%d", author.getHandle(), System.currentTimeMillis())
            : Slugify.slugify(request.title());

    ReflectionContentMediaService.PreparedContent preparedContent =
        contentMediaService.prepareContentForCreate(authorId, request.content().json());

    Reflection reflection =
        Reflection.builder()
            .slug(slug)
            .author(author)
            .title(request.title())
            .content(preparedContent.content())
            .build();
    List<Tag> tags = tagService.addTagsToReflection(reflection, request.tags());

    reflection = reflectionRepository.save(reflection);
    badgeCollectionService.increaseBadges(author, tags);

    for (int i = 0; i < preparedContent.mediaFiles().size(); i++) {
      reflectionMediaFileRepository.save(
          new ReflectionMediaFile(reflection, preparedContent.mediaFiles().get(i), i));
    }

    reflectionRepository.incrementActivityCount(
        author.getId(), LocalDate.ofInstant(reflection.getCreatedAt(), ZoneId.systemDefault()));

    eventPublisher.publishEvent(
        new ReflectionCreatedEvent(reflection.getId(), request.content().text()));

    return new CreateReflectionResponse(reflection.getSlug());
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
  @PreAuthorize("hasPermission(#reflectionId, 'REFLECTION', 'EDIT')")
  public void updateReflectionSummary(Long reflectionId, UpdateReflectionSummaryRequest request) {
    Reflection reflection =
        reflectionRepository
            .findById(reflectionId)
            .orElseThrow(() -> new ReflectionNotFoundException(reflectionId));

    ReflectionSummary summary =
        reflectionSummaryRepository
            .findById(reflectionId)
            .orElseGet(() -> new ReflectionSummary(reflection, request.summary()));

    summary.updateSummary(request.summary());
    reflectionSummaryRepository.save(summary);
  }

  @Transactional
  @PreAuthorize("hasPermission(#reflectionId, 'REFLECTION', 'DELETE')")
  public void deleteReflection(Long reflectionId) {
    if (!reflectionRepository.existsById(reflectionId)) {
      throw new ReflectionNotFoundException(reflectionId);
    }

    Reflection reflection =
        reflectionRepository
            .findByIdWithAuthorAndTags(reflectionId)
            .orElseThrow(() -> new ReflectionNotFoundException(reflectionId));

    List<Tag> tags =
        reflection.getTags().stream().map(reflectionTag -> reflectionTag.getTag()).toList();
    tagService.decrementPostCounts(tags);
    badgeCollectionService.decreaseBadges(reflection.getAuthor(), tags);
    reflectionRepository.delete(reflection);
  }
}
