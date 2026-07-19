package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.constants.PostConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PostContentRequest(
    @NotBlank @Size(max = PostConstants.MAX_CONTENT_TEXT_LENGTH) String text,
    @NotBlank @Size(max = PostConstants.MAX_CONTENT_JSON_LENGTH) String json,
    List<Long> mediaIds) {}
