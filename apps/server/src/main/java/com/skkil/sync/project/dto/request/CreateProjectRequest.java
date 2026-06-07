package com.skkil.sync.project.dto.request;

import com.skkil.sync.project.constants.ProjectConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
    @NotBlank
        @Size(min = ProjectConstants.MIN_HANDLE_LENGTH, max = ProjectConstants.MAX_HANDLE_LENGTH)
        String handle,
    @NotBlank @Size(min = ProjectConstants.MIN_NAME_LENGTH, max = ProjectConstants.MAX_NAME_LENGTH)
        String name) {}
