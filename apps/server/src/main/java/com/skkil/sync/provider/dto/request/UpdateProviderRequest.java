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
  @JsonSubTypes.Type(value = UpdateSchoolRequest.class, name = ProviderType.Constants.SCHOOL)
})
public sealed interface UpdateProviderRequest permits UpdateSchoolRequest {

  ProviderType type();

  String name();

  String description();
}
