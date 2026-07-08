package com.skkil.sync.project.dto.summary;

import com.skkil.sync.project.model.Role;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ProjectInvitationSummary(
    Long id, UserSummary inviter, Role role, Instant expiresAt) {}
