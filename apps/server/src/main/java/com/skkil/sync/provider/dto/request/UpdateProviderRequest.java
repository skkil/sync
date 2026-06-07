package com.skkil.sync.provider.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.provider.company.dto.request.UpdateCompanyRequest;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.contest.dto.request.UpdateContestRequest;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = UpdateCompanyRequest.class, name = ProviderType.Constants.COMPANY),
  @JsonSubTypes.Type(value = UpdateSchoolRequest.class, name = ProviderType.Constants.SCHOOL),
  @JsonSubTypes.Type(value = UpdateLabRequest.class, name = ProviderType.Constants.LAB),
  @JsonSubTypes.Type(value = UpdateContestRequest.class, name = ProviderType.Constants.CONTEST),
})
public interface UpdateProviderRequest {

  ProviderType type();

  String name();

  String description();
}
