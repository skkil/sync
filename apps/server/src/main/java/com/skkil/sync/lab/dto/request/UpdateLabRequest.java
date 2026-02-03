package com.skkil.sync.lab.dto.request;

public record UpdateLabRequest(
    String name,
    String description,
    String oneLineReview,
    String researchArea,
    String detailedResearchField,
    String contactInfo) {}
