package com.skkil.sync.newsletter.dto;

import java.time.LocalDateTime;

public record NewsletterPost(
    Long id,
    String slug,
    String title,
    String excerpt,
    String authorName,
    LocalDateTime createdAt,
    String url) {}
