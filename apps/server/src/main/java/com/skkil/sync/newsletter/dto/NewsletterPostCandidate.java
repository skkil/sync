package com.skkil.sync.newsletter.dto;

import java.time.LocalDateTime;

public record NewsletterPostCandidate(
    Long id,
    String slug,
    String title,
    String content,
    String authorName,
    LocalDateTime createdAt) {}
