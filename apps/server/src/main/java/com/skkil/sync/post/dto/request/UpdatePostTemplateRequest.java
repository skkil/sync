package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.constants.PostConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/** 전체 교체 방식이다 — 수정 폼이 항상 모든 필드를 제출하며, {@code title} 이 null 이면 접두어를 비운다. */
public record UpdatePostTemplateRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) @Nullable String title,
    @NotBlank @Size(max = PostConstants.MAX_TEMPLATE_CONTENT_LENGTH) String content) {}
