package com.skkil.sync.provider.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetLabResponse(
    Long id,
    ProviderType type,
    String name,
    String description,
    String oneLineReview,
    ProfessorInfo professor,
    SchoolInfo school,
    String researchArea,
    String detailedResearchField,
    String contactInfo,
    LocalDateTime createdAt,
    LocalDateTime updatedAt)
    implements GetProviderResponse {

  public record ProfessorInfo(String id, String fullName, String email) {}

  public record SchoolInfo(Long id, String name) {}
}
