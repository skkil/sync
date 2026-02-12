package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.CreateSchoolRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateSchoolRequest;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.dto.response.GetSchoolResponse;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.model.School;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class SchoolService implements ProviderStrategy {

  @Override
  public ProviderType getProviderType() {
    return ProviderType.SCHOOL;
  }

  @Override
  @Transactional
  public Provider createProvider(CreateProviderRequest request) {
    CreateSchoolRequest schoolRequest = (CreateSchoolRequest) request;

    School school =
        School.builder()
            .name(request.name())
            .description(request.description())
            .schoolType(schoolRequest.schoolType())
            .build();

    return school;
  }

  @Override
  public GetProviderResponse toGetProviderResponse(Provider provider) {
    return GetSchoolResponse.builder()
        .id(provider.getId())
        .type(provider.getType())
        .name(provider.getName())
        .description(provider.getDescription())
        .schoolType(((School) provider).getSchoolType())
        .createdAt(LocalDateTime.ofInstant(provider.getCreatedAt(), ZoneId.systemDefault()))
        .updatedAt(LocalDateTime.ofInstant(provider.getUpdatedAt(), ZoneId.systemDefault()))
        .verifiedBy(provider.getVerifiedBy() != null ? provider.getVerifiedBy().getId() : null)
        .build();
  }

  @Override
  @Transactional
  public void updateProvider(Provider provider, UpdateProviderRequest request) {
    School school = (School) provider;
    UpdateSchoolRequest schoolRequest = (UpdateSchoolRequest) request;

    if (request.name() != null) {
      school.setName(request.name());
    }

    if (request.description() != null) {
      school.setDescription(request.description());
    }

    if (schoolRequest.schoolType() != null) {
      school.setSchoolType(schoolRequest.schoolType());
    }
  }
}
