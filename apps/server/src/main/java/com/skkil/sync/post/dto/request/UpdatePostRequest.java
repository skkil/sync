package com.skkil.sync.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest(Long projectId, @NotBlank String content) {}
