package com.skkil.sync.provider.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GetJobPostingsResponse(List<JobPosting> postings) {

  public static record JobPosting(
      String id,
      String jobTitle,
      String jobDescription,
      String location,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {}
}
