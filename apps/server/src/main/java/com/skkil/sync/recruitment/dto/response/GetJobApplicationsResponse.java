package com.skkil.sync.recruitment.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;

public record GetJobApplicationsResponse(OffsetPaginationResponse<Application> applications) {

  public static record Application(Long id, Company company) {}

  public static record Company(Long id, String name) {}
}
