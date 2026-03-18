package com.skkil.sync.experience.service;

import com.skkil.sync.experience.dto.request.CreateReflectionRequest;
import com.skkil.sync.experience.dto.request.UpdateReflectionRequest;
import com.skkil.sync.experience.dto.response.GetReflectionsResponse;
import com.skkil.sync.experience.exception.ReflectionNotFoundException;
import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.model.Reflection;
import com.skkil.sync.experience.repository.ExperienceRepository;
import com.skkil.sync.experience.repository.ReflectionRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionService {

  private final ExperienceRepository experienceRepository;
  private final ReflectionRepository reflectionRepository;

  public ReflectionService(
      ExperienceRepository experienceRepository, ReflectionRepository reflectionRepository) {
    this.experienceRepository = experienceRepository;
    this.reflectionRepository = reflectionRepository;
  }

  @Transactional
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'EDIT')")
  public void createReflection(Long experienceId, CreateReflectionRequest request) {
    Experience experience = experienceRepository.getReferenceById(experienceId);

    Reflection reflection =
        Reflection.builder().experience(experience).content(request.content()).build();

    reflectionRepository.save(reflection);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'READ')")
  public GetReflectionsResponse getReflections(Long experienceId) {
    Experience experience = experienceRepository.getReferenceById(experienceId);
    List<Reflection> reflections = reflectionRepository.findByExperience(experience);

    return new GetReflectionsResponse(
        reflections.stream()
            .map(
                reflection ->
                    GetReflectionsResponse.Reflection.builder()
                        .id(reflection.getId())
                        .content(reflection.getContent())
                        .createdAt(
                            LocalDateTime.ofInstant(
                                reflection.getCreatedAt(), ZoneId.systemDefault()))
                        .build())
            .toList());
  }

  @Transactional
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'EDIT')")
  public void updateReflection(
      Long experienceId, Long reflectionId, UpdateReflectionRequest request) {
    Reflection reflection = getReflection(experienceId, reflectionId);
    reflection.updateContent(request.content());
  }

  @Transactional
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'DELETE')")
  public void deleteReflection(Long experienceId, Long reflectionId) {
    Reflection reflection = getReflection(experienceId, reflectionId);
    reflectionRepository.delete(reflection);
  }

  private Reflection getReflection(Long experienceId, Long reflectionId) {
    return reflectionRepository
        .findByIdAndExperienceId(reflectionId, experienceId)
        .orElseThrow(() -> new ReflectionNotFoundException(reflectionId));
  }
}
