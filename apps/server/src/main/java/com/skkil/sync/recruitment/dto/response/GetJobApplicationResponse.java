package com.skkil.sync.recruitment.dto.response;

import com.skkil.sync.recruitment.enums.JobApplicationStatus;
import lombok.Builder;

@Builder
public record GetJobApplicationResponse(
    Long id,
    JobApplicationStatus status,
    Company company,
    JobDescription jobDescription,
    String notes) {

  @Builder
  public static record Company(Long id, String name) {}

  @Builder
  public static record JobDescription(String title, String description, String location) {}
}
