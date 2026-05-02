package com.skkil.sync.provider.contest.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetContestResponse(
    Long id,
    ProviderType type,
    String name,
    String description,
    Long verifiedBy,
    Provider hostProvider,
    String contactInfo,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isMaintainer)
    implements GetProviderResponse {

  public static record Provider(Long id, ProviderType type, String name) {}
}
