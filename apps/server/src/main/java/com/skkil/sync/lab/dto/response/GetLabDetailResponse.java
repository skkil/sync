package com.skkil.sync.lab.dto.response;

import java.util.List;

public record GetLabDetailResponse(
    Long id,
    String name,
    String description,
    String oneLineReview,
    ProfessorInfo professor,
    SchoolInfo school,
    String researchArea,
    String detailedResearchField,
    String contactInfo,
    List<MemberInfo> members) {

  public record ProfessorInfo(Long id, String fullName, String email) {}

  public record SchoolInfo(Long id, String name) {}

  public record MemberInfo(Long userId, String fullName, String email, String bio, String review) {}
}
