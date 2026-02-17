package com.skkil.sync.review.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateSchoolReviewRequest(
    ProviderType type,
    String review,
    @Min(0) @Max(5) Double academicQuality,
    @Min(0) @Max(5) Double campusFacilities,
    @Min(0) @Max(5) Double studentLife,
    @Min(0) @Max(5) Double valueForMoney)
    implements CreateReviewRequest {}
