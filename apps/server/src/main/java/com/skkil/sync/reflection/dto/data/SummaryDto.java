package com.skkil.sync.reflection.dto.data;

import java.time.LocalDateTime;

public record SummaryDto(
    Long reflectionId, String slug, String title, String displayText, LocalDateTime createdAt) {}
