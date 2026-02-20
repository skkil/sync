package com.skkil.sync.review.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.skkil.sync.provider.constant.ProviderType;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CreateSchoolReviewRequest.class, name = ProviderType.Constants.SCHOOL),
})
public sealed interface CreateReviewRequest permits CreateSchoolReviewRequest {

  ProviderType type();

  String review();
}
