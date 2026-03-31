package com.skkil.sync.recruitment.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateJobApplicationRequest(
    @NotNull String companyId, @NotNull String jobPostingId) {}
