package com.skkil.sync.provider.dto.response;

import lombok.Builder;

@Builder
public record LabSearchResponse(
    Long id, String name, String oneLineReview, String schoolName, String professorName) {}
