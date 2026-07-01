package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.model.PostType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record CreatePostRequest(
    String title,
    @NotNull PostType type,
    @Valid @NotNull Content content,
    List<String> tags,
    Project project) {

  public static record Content(String text, @NotBlank String json, List<Long> mediaIds) {}

  public static record Project(@NotBlank String handle) {}
}
