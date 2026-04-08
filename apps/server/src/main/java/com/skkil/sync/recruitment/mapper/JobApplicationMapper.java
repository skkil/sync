package com.skkil.sync.recruitment.mapper;

import com.skkil.sync.recruitment.dto.data.JobApplicationDto;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

  @Mappings({
    @Mapping(target = "id", source = "application.id"),
    @Mapping(target = "company.id", source = "application.companyId"),
    @Mapping(target = "company.name", source = "application.companyName"),
    @Mapping(target = "jobDescription.title", source = "application.jobTitle"),
    @Mapping(target = "jobDescription.description", source = "application.jobDescription"),
    @Mapping(target = "jobDescription.location", source = "application.jobLocation")
  })
  GetJobApplicationResponse toGetJobApplicationResponse(JobApplicationDto application);
}
