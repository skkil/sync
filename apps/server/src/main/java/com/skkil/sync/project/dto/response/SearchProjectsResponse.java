package com.skkil.sync.project.dto.response;

import java.util.List;

public record SearchProjectsResponse(List<Project> projects) {

  public record Project(Long id, String handle, String name) {}
}
