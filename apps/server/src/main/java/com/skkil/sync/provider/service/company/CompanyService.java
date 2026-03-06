package com.skkil.sync.provider.service.company;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateCompanyRequest;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateCompanyRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetCompanyResponse;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.model.company.Company;
import com.skkil.sync.provider.service.ProviderStrategy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService implements ProviderStrategy {

  @Override
  public ProviderType getProviderType() {
    return ProviderType.COMPANY;
  }

  @Override
  @Transactional
  public Provider createProvider(CreateProviderRequest request) {
    CreateCompanyRequest companyRequest = (CreateCompanyRequest) request;

    Company company =
        Company.builder()
            .name(request.name())
            .description(request.description())
            .industry(companyRequest.industry())
            .build();

    return company;
  }

  @Override
  public GetProviderResponse toGetProviderResponse(Provider provider, boolean isMaintainer) {
    Company company = (Company) provider;

    return GetCompanyResponse.builder()
        .id(provider.getId())
        .type(provider.getType())
        .name(provider.getName())
        .description(provider.getDescription())
        .contactInfo(provider.getContactInfo())
        .industry(company.getIndustry())
        .createdAt(LocalDateTime.ofInstant(provider.getCreatedAt(), ZoneId.systemDefault()))
        .updatedAt(LocalDateTime.ofInstant(provider.getUpdatedAt(), ZoneId.systemDefault()))
        .verifiedBy(provider.getVerifiedBy() != null ? provider.getVerifiedBy().getId() : null)
        .isMaintainer(isMaintainer)
        .build();
  }

  @Override
  @Transactional
  public void updateProvider(Provider provider, UpdateProviderRequest request) {
    Company company = (Company) provider;
    UpdateCompanyRequest companyRequest = (UpdateCompanyRequest) request;
    company.updateFields(request.name(), request.description(), companyRequest.industry());
  }
}
