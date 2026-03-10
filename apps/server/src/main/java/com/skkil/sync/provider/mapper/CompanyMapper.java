package com.skkil.sync.provider.mapper;

import com.skkil.sync.provider.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.model.company.JobPosting;
import java.time.Instant;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

  GetJobPostingsResponse.JobPosting toJobPostingResponse(JobPosting jobPosting);

  default LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
  }
}
