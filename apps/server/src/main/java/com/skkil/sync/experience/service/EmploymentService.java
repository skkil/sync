package com.skkil.sync.experience.service;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.dto.request.CreateExperienceRequest;
import com.skkil.sync.experience.model.Employment;
import com.skkil.sync.experience.model.Experience;
import org.springframework.stereotype.Service;

@Service
final class EmploymentService implements ExperienceStrategy {

  @Override
  public ExperienceType getExperienceType() {
    return ExperienceType.EMPLOYMENT;
  }

  @Override
  public Experience createExperience(CreateExperienceRequest request) {
    return new Employment();
  }
}
