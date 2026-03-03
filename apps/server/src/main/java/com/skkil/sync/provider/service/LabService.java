package com.skkil.sync.provider.service;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateLabRequest;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateLabRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.GetLabResponse;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.exception.ProviderNotFoundException;
import com.skkil.sync.provider.model.Lab;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.provider.model.School;
import com.skkil.sync.provider.repository.LabRepository;
import com.skkil.sync.provider.repository.SchoolRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabService implements ProviderStrategy {

  private final LabRepository labRepository;
  private final SchoolRepository schoolRepository;

  public LabService(LabRepository labRepository, SchoolRepository schoolRepository) {
    this.labRepository = labRepository;
    this.schoolRepository = schoolRepository;
  }

  @Override
  public ProviderType getProviderType() {
    return ProviderType.LAB;
  }

  @Override
  @Transactional
  public Provider createProvider(CreateProviderRequest request) {
    CreateLabRequest labRequest = (CreateLabRequest) request;

    School school =
        (School)
            schoolRepository
                .findById(labRequest.schoolId())
                .orElseThrow(() -> new ProviderNotFoundException(labRequest.schoolId()));

    Lab lab =
        Lab.builder()
            .name(request.name())
            .description(request.description())
            .professorName(labRequest.professorName())
            .school(school)
            .researchArea(labRequest.researchArea())
            .detailedResearchField(labRequest.detailedResearchField())
            .contactInfo(labRequest.contactInfo())
            .oneLineReview(labRequest.oneLineReview())
            .build();

    return labRepository.save(lab);
  }

  @Override
  public GetProviderResponse toGetProviderResponse(Provider provider, boolean isMaintainer) {
    Lab lab = (Lab) provider;

    return GetLabResponse.builder()
        .id(lab.getId())
        .type(lab.getType())
        .name(lab.getName())
        .description(lab.getDescription())
        .contactInfo(lab.getContactInfo())
        .oneLineReview(lab.getOneLineReview())
        .professor(new GetLabResponse.ProfessorInfo(lab.getProfessorName(), null, null))
        .school(new GetLabResponse.SchoolInfo(lab.getSchool().getId(), lab.getSchool().getName()))
        .researchArea(lab.getResearchArea())
        .detailedResearchField(lab.getDetailedResearchField())
        .createdAt(LocalDateTime.ofInstant(lab.getCreatedAt(), ZoneId.systemDefault()))
        .updatedAt(LocalDateTime.ofInstant(lab.getUpdatedAt(), ZoneId.systemDefault()))
        .verifiedBy(lab.getVerifiedBy() != null ? lab.getVerifiedBy().getId() : null)
        .isMaintainer(isMaintainer)
        .build();
  }

  @Override
  @Transactional
  public void updateProvider(Provider provider, UpdateProviderRequest request) {
    Lab lab = (Lab) provider;
    UpdateLabRequest labRequest = (UpdateLabRequest) request;

    if (request.name() != null) {
      lab.setName(request.name());
    }
    if (request.description() != null) {
      lab.setDescription(request.description());
    }

    if (labRequest.oneLineReview() != null) {
      lab.setOneLineReview(labRequest.oneLineReview());
    }
    if (labRequest.researchArea() != null) {
      lab.setResearchArea(labRequest.researchArea());
    }
    if (labRequest.detailedResearchField() != null) {
      lab.setDetailedResearchField(labRequest.detailedResearchField());
    }
  }
}
