package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.model.Role;
import java.util.List;
import lombok.Builder;

public record GetProjectTeammatesResponse(List<Teammate> teammates) {

  @Builder
  public record Teammate(String handle, String name, Role role, String profileImageUrl) {}
}
