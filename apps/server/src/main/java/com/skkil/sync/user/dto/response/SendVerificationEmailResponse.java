package com.skkil.sync.user.dto.response;

import java.time.Instant;

public record SendVerificationEmailResponse(Instant expiresAt, long validForSeconds) {}
