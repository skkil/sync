package com.skkil.sync.experience.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.constant.ExperienceVisibility;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record GetExperiencesResponse(List<Experience> experiences) {

  public static record Provider(Long id, String name) {}

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXISTING_PROPERTY,
      property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = EducationExperience.class, name = "EDUCATION"),
    @JsonSubTypes.Type(value = EmploymentExperience.class, name = "EMPLOYMENT")
  })
  public interface Experience {
    ExperienceType type();

    ExperienceVisibility visibility();

    Provider provider();

    Long id();

    LocalDateTime startDate();

    LocalDateTime endDate();
  }

  public static record EducationExperience(
      ExperienceType type,
      ExperienceVisibility visibility,
      Provider provider,
      Long id,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String major,
      BigDecimal gpa)
      implements Experience {}

  public static record EmploymentExperience(
      ExperienceType type,
      ExperienceVisibility visibility,
      Provider provider,
      Long id,
      LocalDateTime startDate,
      LocalDateTime endDate)
      implements Experience {}
}
