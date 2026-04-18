package com.skkil.sync.experience.repository;

import com.skkil.sync.experience.dto.data.ProjectExperienceDto;
import java.util.List;

interface CustomExperienceRepository {

  List<ProjectExperienceDto> findProjectExperiencesByUserId(Long userId);
}
