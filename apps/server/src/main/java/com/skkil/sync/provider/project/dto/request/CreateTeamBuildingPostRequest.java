package com.skkil.sync.provider.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamBuildingPostRequest(
    @NotBlank @Size(max = 255) String title, String content) {}
