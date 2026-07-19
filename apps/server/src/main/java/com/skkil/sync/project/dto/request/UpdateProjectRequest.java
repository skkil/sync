package com.skkil.sync.project.dto.request;

import com.skkil.sync.project.constants.ProjectConstants;
import com.skkil.sync.project.validator.NotReservedHandle;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
    String description,
    String website,
    String iconMediaId,
    Boolean removeIcon,
    @Size(min = ProjectConstants.MIN_NAME_LENGTH, max = ProjectConstants.MAX_NAME_LENGTH)
        String name,
    @Size(min = ProjectConstants.MIN_HANDLE_LENGTH, max = ProjectConstants.MAX_HANDLE_LENGTH)
        @NotReservedHandle
        String handle) {}
