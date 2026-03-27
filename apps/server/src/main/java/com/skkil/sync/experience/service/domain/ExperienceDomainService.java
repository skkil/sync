package com.skkil.sync.experience.service.domain;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.exception.ExperienceNotFoundException;
import com.skkil.sync.experience.model.Experience;
import com.skkil.sync.experience.repository.ExperienceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceDomainService {

  private final ExperienceRepository experienceRepository;

  public ExperienceDomainService(ExperienceRepository experienceRepository) {
    this.experienceRepository = experienceRepository;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#experienceId, 'EXPERIENCE', 'EDIT')")
  public Experience getExperience(Long experienceId, ExperienceType experienceType) {
    Experience experience =
        experienceRepository
            .findById(experienceId)
            .orElseThrow(() -> new ExperienceNotFoundException(experienceId));

    if (!experience.getType().equals(experienceType)) {
      throw new ExperienceNotFoundException(experienceId);
    }

    return experience;
  }
}
