package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.constants.PostConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record CreatePostTemplateRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) @Nullable String title,
    @NotBlank @Size(max = PostConstants.MAX_TEMPLATE_CONTENT_LENGTH) String content) {}
