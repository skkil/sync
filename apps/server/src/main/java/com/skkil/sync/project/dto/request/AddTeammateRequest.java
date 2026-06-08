package com.skkil.sync.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddTeammateRequest(@NotBlank String teammateHandle) {}
