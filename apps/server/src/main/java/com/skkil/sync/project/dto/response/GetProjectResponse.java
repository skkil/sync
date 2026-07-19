package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.dto.summary.ProjectSummary;
import com.skkil.sync.project.dto.summary.ProjectTeammateSummary;
import com.skkil.sync.project.model.Role;
import java.util.List;
import lombok.Builder;

@Builder
public record GetProjectResponse(
    ProjectSummary summary,
    List<ProjectTeammateSummary> teammates,
    boolean hasMoreTeammates,
    boolean isViewer,
    Role role,
    boolean isFollowing,
    boolean hasPendingInvitation,
    List<Activity> recentActivities) {

  public record Activity(String id, String timestamp, String text) {}
}
