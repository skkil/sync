package com.skkil.sync.project.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record GetProjectResponse(
    String handle,
    String name,
    String description,
    boolean isPublic,
    List<Teammate> teammates,
    boolean hasMoreTeammates,
    boolean isAdmin,
    boolean isMember,
    List<Activity> recentActivities) {

  public record Teammate(String id, boolean isOwner) {}

  public record Activity(String id, String timestamp, String text) {}
}
