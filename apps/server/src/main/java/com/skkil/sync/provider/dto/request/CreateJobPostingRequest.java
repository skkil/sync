package com.skkil.sync.provider.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateJobPostingRequest(
    @NotNull @Size(min = 1, max = 255) String jobTitle,
    @NotNull String jobDescription,
    @Size(max = 255) String location) {}
