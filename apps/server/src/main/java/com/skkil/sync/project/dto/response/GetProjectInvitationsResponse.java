package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.dto.summary.ProjectInvitationSummary;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.util.List;

public record GetProjectInvitationsResponse(List<Invitation> invitations) {

  public record Invitation(ProjectInvitationSummary invitation, UserSummary invitee) {}
}
