package com.skkil.sync.experience.service;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.dto.request.CreateEducationRequest;
import com.skkil.sync.experience.dto.request.CreateExperienceRequest;
import com.skkil.sync.experience.model.Education;
import com.skkil.sync.experience.model.Experience;
import org.springframework.stereotype.Service;

@Service
final class EducationService implements ExperienceStrategy {

  @Override
  public ExperienceType getExperienceType() {
    return ExperienceType.EDUCATION;
  }

  @Override
  public Experience createExperience(CreateExperienceRequest request) {
    CreateEducationRequest educationRequest = (CreateEducationRequest) request;
    Education education =
        Education.builder().major(educationRequest.major()).gpa(educationRequest.gpa()).build();

    return education;
  }
}
