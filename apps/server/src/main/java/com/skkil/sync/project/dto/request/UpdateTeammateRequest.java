package com.skkil.sync.project.dto.request;

import com.skkil.sync.project.model.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateTeammateRequest(@NotNull Role role) {}
