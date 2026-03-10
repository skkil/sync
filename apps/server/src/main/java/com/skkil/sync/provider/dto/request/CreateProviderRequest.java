package com.skkil.sync.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.provider.constant.ProviderType;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CreateCompanyRequest.class, name = ProviderType.Constants.COMPANY),
  @JsonSubTypes.Type(value = CreateSchoolRequest.class, name = ProviderType.Constants.SCHOOL),
  @JsonSubTypes.Type(value = CreateLabRequest.class, name = ProviderType.Constants.LAB),
  @JsonSubTypes.Type(value = CreateContestRequest.class, name = ProviderType.Constants.CONTEST),
  @JsonSubTypes.Type(value = CreateProjectRequest.class, name = ProviderType.Constants.PROJECT)
})
public sealed interface CreateProviderRequest
    permits CreateCompanyRequest,
        CreateSchoolRequest,
        CreateLabRequest,
        CreateContestRequest,
        CreateProjectRequest {

  ProviderType type();

  String name();

  String description();
}
