package com.skkil.sync.badge.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

public record GetBadgeCollectionsResponse(List<Badge> badges) {

  @Builder
  public static record Badge(Long id, Tag tag, Long postCount, Integer level, Instant createdAt) {}

  @Builder
  public static record Tag(Long id, String name, String description, String imageUrl) {}
}
