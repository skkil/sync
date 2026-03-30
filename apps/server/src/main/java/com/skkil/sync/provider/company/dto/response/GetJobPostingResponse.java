package com.skkil.sync.provider.company.dto.response;

import java.time.LocalDateTime;

public record GetJobPostingResponse(
    String id,
    Company company,
    String jobTitle,
    String jobDescription,
    String location,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static record Company(String id, String name) {}
}
