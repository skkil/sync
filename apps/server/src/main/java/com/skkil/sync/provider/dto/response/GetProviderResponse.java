package com.skkil.sync.provider.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.project.dto.response.GetProjectResponse;
import java.time.LocalDateTime;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = GetCompanyResponse.class, name = ProviderType.Constants.COMPANY),
  @JsonSubTypes.Type(value = GetSchoolResponse.class, name = ProviderType.Constants.SCHOOL),
  @JsonSubTypes.Type(value = GetLabResponse.class, name = ProviderType.Constants.LAB),
  @JsonSubTypes.Type(value = GetContestResponse.class, name = ProviderType.Constants.CONTEST),
  @JsonSubTypes.Type(value = GetProjectResponse.class, name = ProviderType.Constants.PROJECT)
})
public interface GetProviderResponse {

  Long id();

  ProviderType type();

  String name();

  String description();

  String contactInfo();

  LocalDateTime createdAt();

  LocalDateTime updatedAt();

  Long verifiedBy();

  boolean isMaintainer();
}
