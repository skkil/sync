package com.skkil.sync.experience.dto.response;

import java.util.List;

public record GetProjectExperiencesResponse(List<ProjectExperience> experiences) {

  public static record ProjectExperience(Long id, Long projectId, String projectName) {}
}
