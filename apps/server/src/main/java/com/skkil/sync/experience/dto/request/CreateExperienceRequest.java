package com.skkil.sync.experience.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.experience.constant.ExperienceType;
import java.time.LocalDateTime;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(
      value = CreateEducationRequest.class,
      name = ExperienceType.Constants.EDUCATION),
  @JsonSubTypes.Type(
      value = CreateEmploymentRequest.class,
      name = ExperienceType.Constants.EMPLOYMENT)
})
public sealed interface CreateExperienceRequest
    permits CreateEducationRequest, CreateEmploymentRequest {

  ExperienceType type();

  Long providerId();

  LocalDateTime startDate();

  LocalDateTime endDate();
}
