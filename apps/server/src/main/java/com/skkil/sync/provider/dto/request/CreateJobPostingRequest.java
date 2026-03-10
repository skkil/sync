package com.skkil.sync.provider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateJobPostingRequest(
    @NotBlank @Size(max = 255) String jobTitle,
    @NotBlank String jobDescription,
    @Size(max = 255) String location) {}
