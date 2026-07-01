package com.skkil.sync.post.dto.data;

import java.time.OffsetDateTime;

public record SummaryDto(
    Long postId, String slug, String title, String displayText, OffsetDateTime createdAt) {}
