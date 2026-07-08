package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.dto.summary.ProjectInvitationSummary;
import com.skkil.sync.project.dto.summary.ProjectSummary;
import java.util.List;

public record GetMyProjectInvitationsResponse(List<Invitation> invitations) {

  public record Invitation(
      ProjectInvitationSummary invitation, String token, ProjectSummary project) {}
}
