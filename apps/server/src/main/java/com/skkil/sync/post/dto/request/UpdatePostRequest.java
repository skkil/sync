package com.skkil.sync.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest(@NotBlank String content) {}
