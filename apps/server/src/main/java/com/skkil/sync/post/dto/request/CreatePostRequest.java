package com.skkil.sync.post.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreatePostRequest(
    String title, @Valid @NotNull Content content, List<String> tags, Long projectId) {

  public static record Content(@NotBlank String text, @NotBlank String json) {}
}
