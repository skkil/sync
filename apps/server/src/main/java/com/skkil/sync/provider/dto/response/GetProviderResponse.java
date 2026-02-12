package com.skkil.sync.provider.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.provider.constant.ProviderType;
import java.time.LocalDateTime;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = GetSchoolResponse.class, name = ProviderType.Constants.SCHOOL),
  @JsonSubTypes.Type(value = GetLabResponse.class, name = ProviderType.Constants.LAB)
})
public sealed interface GetProviderResponse permits GetSchoolResponse, GetLabResponse {

  Long id();

  ProviderType type();

  String name();

  String description();

  String contactInfo();

  LocalDateTime createdAt();

  LocalDateTime updatedAt();

  Long verifiedBy();
}
