package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.model.Role;
import java.util.List;
import lombok.Builder;

@Builder
public record GetProjectResponse(
    String handle,
    String name,
    String description,
    String website,
    boolean isPublic,
    List<Teammate> teammates,
    boolean hasMoreTeammates,
    Role role,
    List<Activity> recentActivities) {

  @Builder
  public record Teammate(String handle, String name, Role role) {}

  public record Activity(String id, String timestamp, String text) {}
}
