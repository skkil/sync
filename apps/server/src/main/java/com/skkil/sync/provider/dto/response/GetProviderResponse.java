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
  @JsonSubTypes.Type(value = GetSchoolResponse.class, name = ProviderType.Constants.SCHOOL)
})
public sealed interface GetProviderResponse permits GetSchoolResponse {

  Long id();

  ProviderType type();

  String name();

  String description();

  LocalDateTime createdAt();

  LocalDateTime updatedAt();

  Long verifiedBy();
}
