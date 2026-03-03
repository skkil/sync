package com.skkil.sync.experience.mapper;

import com.skkil.sync.experience.dto.request.UpdateEducationRequest;
import com.skkil.sync.experience.dto.request.UpdateEmploymentRequest;
import com.skkil.sync.experience.dto.request.UpdateExperienceRequest;
import com.skkil.sync.experience.dto.response.GetExperiencesResponse;
import com.skkil.sync.experience.model.Education;
import com.skkil.sync.experience.model.Employment;
import com.skkil.sync.experience.model.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExperienceMapper {

  default GetExperiencesResponse.Experience toExperienceResponse(Experience experience) {
    if (experience instanceof Education education) {
      return toEducationResponse(education);
    } else if (experience instanceof Employment employment) {
      return toEmploymentResponse(employment);
    } else {
      throw new IllegalArgumentException("Unsupported experience type");
    }
  }

  default void updateExperience(UpdateExperienceRequest request, Experience experience) {
    if (experience instanceof Education education
        && request instanceof UpdateEducationRequest educationRequest) {
      updateEducation(educationRequest, education);
    } else if (experience instanceof Employment employment
        && request instanceof UpdateEmploymentRequest employmentRequest) {
      updateEmployment(employmentRequest, employment);
    } else {
      throw new IllegalArgumentException("Experience and request types do not match");
    }
  }

  @Mapping(source = "provider.id", target = "provider.id")
  @Mapping(source = "provider.name", target = "provider.name")
  GetExperiencesResponse.EducationExperience toEducationResponse(Education education);

  @Mapping(source = "provider.id", target = "provider.id")
  @Mapping(source = "provider.name", target = "provider.name")
  GetExperiencesResponse.EmploymentExperience toEmploymentResponse(Employment employment);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "provider", ignore = true)
  void updateEducation(UpdateEducationRequest request, @MappingTarget Education education);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "provider", ignore = true)
  void updateEmployment(UpdateEmploymentRequest request, @MappingTarget Employment employment);
}
