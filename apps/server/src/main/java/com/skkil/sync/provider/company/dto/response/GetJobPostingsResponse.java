package com.skkil.sync.provider.company.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.PaginationResponse;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetJobPostingsResponse(PaginationResponse<JobPosting> postings) {

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
