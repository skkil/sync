package com.skkil.sync.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(@NotBlank @Size(max = 10000) String content) {}
