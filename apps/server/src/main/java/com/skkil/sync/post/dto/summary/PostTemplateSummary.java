package com.skkil.sync.post.dto.summary;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record PostTemplateSummary(
    String externalId, String name, @Nullable String title, String content, Instant updatedAt) {}
