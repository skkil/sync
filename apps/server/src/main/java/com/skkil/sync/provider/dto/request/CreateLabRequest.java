package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLabRequest(
    @NotNull ProviderType type,
    @NotBlank(message = "Lab name cannot be blank") String name,
    String description,
    String oneLineReview,
    String professorName,
    @NotNull(message = "School ID cannot be null") Long schoolId,
    String contactInfo,
    String researchArea,
    String detailedResearchField)
    implements CreateProviderRequest {

  @AssertTrue(message = "Provider type must be LAB")
  public boolean isLabType() {
    return type == ProviderType.LAB;
  }
}
