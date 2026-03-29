package com.skkil.sync.provider.project.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.project.dto.request.UpdateProjectRequest;
import com.skkil.sync.provider.project.dto.response.GetProjectResponse;
import com.skkil.sync.provider.project.model.Project;
import com.skkil.sync.provider.service.ProviderStrategy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ProjectService implements ProviderStrategy {

  @Override
  public ProviderType getProviderType() {
    return ProviderType.PROJECT;
  }

  @Override
  @Transactional
  public Provider createProvider(CreateProviderRequest request) {
    Project project =
        Project.builder().name(request.name()).description(request.description()).build();

    return project;
  }

  @Override
  public GetProviderResponse toGetProviderResponse(Provider provider, boolean isMaintainer) {
    return GetProjectResponse.builder()
        .id(provider.getId())
        .type(provider.getType())
        .name(provider.getName())
        .description(provider.getDescription())
        .contactInfo(provider.getContactInfo())
        .createdAt(LocalDateTime.ofInstant(provider.getCreatedAt(), ZoneId.systemDefault()))
        .updatedAt(LocalDateTime.ofInstant(provider.getUpdatedAt(), ZoneId.systemDefault()))
        .verifiedBy(provider.getVerifiedBy() != null ? provider.getVerifiedBy().getId() : null)
        .isMaintainer(isMaintainer)
        .build();
  }

  @Override
  @Transactional
  public void updateProvider(Provider provider, UpdateProviderRequest request) {
    Project project = (Project) provider;
    UpdateProjectRequest projectRequest = (UpdateProjectRequest) request;

    if (request.name() != null) {
      project.setName(request.name());
    }

    if (request.description() != null) {
      project.setDescription(request.description());
    }

    if (projectRequest.contactInfo() != null) {
      project.setContactInfo(projectRequest.contactInfo());
    }
  }
}
