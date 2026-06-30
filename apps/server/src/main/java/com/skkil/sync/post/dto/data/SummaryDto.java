package com.skkil.sync.post.dto.data;

import java.time.LocalDateTime;

public record SummaryDto(
    Long postId, String slug, String title, String displayText, LocalDateTime createdAt) {}
