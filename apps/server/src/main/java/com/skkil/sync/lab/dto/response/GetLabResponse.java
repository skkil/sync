package com.skkil.sync.lab.dto.response;

public record GetLabResponse(
    Long id,
    String name,
    String description,
    String oneLineReview,
    ProfessorInfo professor,
    SchoolInfo school,
    String researchArea,
    String detailedResearchField,
    String contactInfo) {

  public record ProfessorInfo(Long id, String fullName, String email) {}

  public record SchoolInfo(Long id, String name) {}
}
