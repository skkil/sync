package com.skkil.sync.experience.service;

import com.skkil.sync.experience.dto.response.GetProjectExperiencesResponse;
import com.skkil.sync.experience.repository.ExperienceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceQueryService {

  private final ExperienceRepository experienceRepository;

  public ExperienceQueryService(ExperienceRepository experienceRepository) {
    this.experienceRepository = experienceRepository;
  }

  @Transactional(readOnly = true)
  public GetProjectExperiencesResponse getProjectExperiences(Long requesterId, Long userId) {
    var experiences =
        experienceRepository.findProjectExperiencesByUserId(userId).stream()
            .map(
                dto ->
                    new GetProjectExperiencesResponse.ProjectExperience(
                        dto.id(), dto.projectId(), dto.projectName()))
            .toList();

    return new GetProjectExperiencesResponse(experiences);
  }
}
