package com.skkil.sync.provider.project.dto.query;

import java.time.Instant;
import lombok.Builder;

@Builder
public record TeamBuildingPostQuery(Instant createdAfter, Long afterId, Long projectId) {}
