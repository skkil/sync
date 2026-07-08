package com.skkil.sync.project.dto.summary;

import com.skkil.sync.project.model.Role;
import com.skkil.sync.user.dto.summary.UserSummary;
import lombok.Builder;

@Builder
public record ProjectTeammateSummary(UserSummary user, Role role) {}
