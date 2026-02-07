package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLabRequest(
    @NotBlank(message = "Lab name cannot be blank") String name,
    String description,
    String oneLineReview,
    String professorId,
    @NotNull(message = "School ID cannot be null") Long schoolId,
    String contactInfo,
    String researchArea,
    String detailedResearchField)
    implements CreateProviderRequest {

  @Override
  public ProviderType type() {
    return ProviderType.LAB;
  }
}
