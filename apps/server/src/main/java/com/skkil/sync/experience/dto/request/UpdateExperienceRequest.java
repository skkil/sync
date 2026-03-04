package com.skkil.sync.experience.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.constant.ExperienceVisibility;
import java.time.LocalDateTime;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = UpdateEducationRequest.class,
      name = ExperienceType.Constants.EDUCATION),
  @JsonSubTypes.Type(
      value = UpdateEmploymentRequest.class,
      name = ExperienceType.Constants.EMPLOYMENT)
})
public sealed interface UpdateExperienceRequest
    permits UpdateEducationRequest, UpdateEmploymentRequest {

  ExperienceType type();

  Long providerId();

  LocalDateTime startDate();

  LocalDateTime endDate();

  ExperienceVisibility visibility();
}
