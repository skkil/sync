package com.skkil.sync.post.dto.data;

import java.time.OffsetDateTime;

public record PostRecommendationCandidate(Long id, OffsetDateTime createdAt, Long likeCount) {}
