package com.skkil.sync.provider.company.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record GetJobPostingsResponse(Page<JobPosting> postings) {

  public static record JobPosting(
      String id,
      Company company,
      String jobTitle,
      String jobDescription,
      String location,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {}

  public static record Company(String id, String name) {}
}
