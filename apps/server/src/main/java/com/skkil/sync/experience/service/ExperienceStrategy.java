package com.skkil.sync.experience.service;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.dto.request.CreateExperienceRequest;
import com.skkil.sync.experience.model.Experience;

sealed interface ExperienceStrategy permits EducationService, EmploymentService {

  ExperienceType getExperienceType();

  Experience createExperience(CreateExperienceRequest request);
}
