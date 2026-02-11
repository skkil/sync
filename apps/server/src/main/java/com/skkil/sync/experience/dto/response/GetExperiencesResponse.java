package com.skkil.sync.experience.dto.response;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.constant.ExperienceVisibility;
import java.time.LocalDateTime;
import java.util.List;

public record GetExperiencesResponse(List<Experience> experiences) {

  public static record Experience(
      ExperienceType type,
      ExperienceVisibility visibility,
      Provider provider,
      Long id,
      LocalDateTime startDate,
      LocalDateTime endDate) {}

  public static record Provider(Long id, String name) {}
}
