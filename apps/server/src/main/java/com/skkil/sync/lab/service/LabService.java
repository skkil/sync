package com.skkil.sync.lab.service;

import com.skkil.sync.lab.dto.request.AddMemberReviewRequest;
import com.skkil.sync.lab.dto.request.CreateLabRequest;
import com.skkil.sync.lab.dto.request.UpdateLabRequest;
import com.skkil.sync.lab.dto.response.CreateLabResponse;
import com.skkil.sync.lab.dto.response.GetLabDetailResponse;
import com.skkil.sync.lab.dto.response.GetLabResponse;
import com.skkil.sync.lab.dto.response.LabMemberResponse;
import com.skkil.sync.lab.dto.response.LabSearchResponse;
import com.skkil.sync.lab.exception.LabNotFoundException;
import com.skkil.sync.lab.exception.ProfessorNotFoundException;
import com.skkil.sync.lab.model.Lab;
import com.skkil.sync.lab.model.LabMember;
import com.skkil.sync.lab.repository.LabRepository;
import com.skkil.sync.provider.model.School;
import com.skkil.sync.provider.repository.SchoolRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabService {

  private final LabRepository labRepository;
  private final UserRepository userRepository;
  private final SchoolRepository schoolRepository;

  public LabService(
      LabRepository labRepository,
      UserRepository userRepository,
      SchoolRepository schoolRepository) {
    this.labRepository = labRepository;
    this.userRepository = userRepository;
    this.schoolRepository = schoolRepository;
  }

  @Transactional
  public CreateLabResponse createLab(CreateLabRequest request) {
    User professor =
        userRepository
            .findById(request.professorId())
            .orElseThrow(() -> new ProfessorNotFoundException(request.professorId()));

    School school =
        schoolRepository
            .findById(request.schoolId())
            .orElseThrow(() -> new RuntimeException("School not found"));

    Lab lab =
        Lab.builder()
            .name(request.name())
            .description(request.description())
            .oneLineReview(request.oneLineReview())
            .professor(professor)
            .school(school)
            .researchArea(request.researchArea())
            .detailedResearchField(request.detailedResearchField())
            .contactInfo(request.contactInfo())
            .build();

    Lab savedLab = labRepository.save(lab);
    return new CreateLabResponse(savedLab.getId());
  }

  @Transactional(readOnly = true)
  public GetLabResponse getLab(Long id) {
    Lab lab = labRepository.findById(id).orElseThrow(() -> new LabNotFoundException(id));

    return new GetLabResponse(
        lab.getId(),
        lab.getName(),
        lab.getDescription(),
        lab.getOneLineReview(),
        new GetLabResponse.ProfessorInfo(
            lab.getProfessor().getId(),
            lab.getProfessor().getFullName(),
            lab.getProfessor().getEmail()),
        new GetLabResponse.SchoolInfo(lab.getSchool().getId(), lab.getSchool().getName()),
        lab.getResearchArea(),
        lab.getDetailedResearchField(),
        lab.getContactInfo());
  }

  @Transactional(readOnly = true)
  public GetLabDetailResponse getLabDetail(Long id) {
    Lab lab = labRepository.findById(id).orElseThrow(() -> new LabNotFoundException(id));

    var memberInfos =
        lab.getMembers().stream()
            .map(
                labMember ->
                    new GetLabDetailResponse.MemberInfo(
                        labMember.getUser().getId(),
                        labMember.getUser().getFullName(),
                        labMember.getUser().getEmail(),
                        labMember.getUser().getBio(),
                        labMember.getReview()))
            .collect(Collectors.toList());

    return new GetLabDetailResponse(
        lab.getId(),
        lab.getName(),
        lab.getDescription(),
        lab.getOneLineReview(),
        new GetLabDetailResponse.ProfessorInfo(
            lab.getProfessor().getId(),
            lab.getProfessor().getFullName(),
            lab.getProfessor().getEmail()),
        new GetLabDetailResponse.SchoolInfo(lab.getSchool().getId(), lab.getSchool().getName()),
        lab.getResearchArea(),
        lab.getDetailedResearchField(),
        lab.getContactInfo(),
        memberInfos);
  }

  @Transactional
  public void updateLab(Long id, UpdateLabRequest request) {
    Lab lab = labRepository.findById(id).orElseThrow(() -> new LabNotFoundException(id));

    if (request.name() != null) {
      lab.setName(request.name());
    }
    if (request.description() != null) {
      lab.setDescription(request.description());
    }
    if (request.oneLineReview() != null) {
      lab.setOneLineReview(request.oneLineReview());
    }
    if (request.researchArea() != null) {
      lab.setResearchArea(request.researchArea());
    }
    if (request.detailedResearchField() != null) {
      lab.setDetailedResearchField(request.detailedResearchField());
    }
    if (request.contactInfo() != null) {
      lab.setContactInfo(request.contactInfo());
    }

    labRepository.save(lab);
  }

  @Transactional
  public void deleteLab(Long id) {
    Lab lab = labRepository.findById(id).orElseThrow(() -> new LabNotFoundException(id));
    labRepository.delete(lab);
  }

  @Transactional(readOnly = true)
  public Page<LabSearchResponse> searchLabs(String keyword, Pageable pageable) {
    return labRepository
        .searchByKeyword(keyword, pageable)
        .map(
            lab ->
                new LabSearchResponse(
                    lab.getId(),
                    lab.getName(),
                    lab.getOneLineReview(),
                    lab.getSchool().getName(),
                    lab.getProfessor().getFullName()));
  }

  @Transactional(readOnly = true)
  public Page<LabSearchResponse> getLabsByProfessor(Long professorId, Pageable pageable) {
    return labRepository
        .findByProfessorId(professorId, pageable)
        .map(
            lab ->
                new LabSearchResponse(
                    lab.getId(),
                    lab.getName(),
                    lab.getOneLineReview(),
                    lab.getSchool().getName(),
                    lab.getProfessor().getFullName()));
  }

  @Transactional
  public void addMember(Long labId, Long userId) {
    Lab lab = labRepository.findById(labId).orElseThrow(() -> new LabNotFoundException(labId));
    User user =
        userRepository.findById(userId).orElseThrow(() -> new ProfessorNotFoundException(userId));

    lab.addMember(user);
    labRepository.save(lab);
  }

  @Transactional
  public LabMemberResponse addMemberWithReview(
      Long labId, Long userId, AddMemberReviewRequest request) {
    Lab lab = labRepository.findById(labId).orElseThrow(() -> new LabNotFoundException(labId));
    User user =
        userRepository.findById(userId).orElseThrow(() -> new ProfessorNotFoundException(userId));

    lab.addMember(user, request.review());
    Lab savedLab = labRepository.save(lab);

    LabMember labMember =
        savedLab.getMembers().stream()
            .filter(member -> member.getUser().getId().equals(userId))
            .findFirst()
            .orElseThrow();

    return new LabMemberResponse(labId, userId, user.getFullName(), labMember.getReview());
  }

  @Transactional
  public void updateMemberReview(Long labId, Long userId, AddMemberReviewRequest request) {
    Lab lab = labRepository.findById(labId).orElseThrow(() -> new LabNotFoundException(labId));

    LabMember labMember =
        lab.getMembers().stream()
            .filter(member -> member.getUser().getId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Member not found in this lab"));

    labMember.setReview(request.review());
    labRepository.save(lab);
  }

  @Transactional
  public void removeMember(Long labId, Long userId) {
    Lab lab = labRepository.findById(labId).orElseThrow(() -> new LabNotFoundException(labId));
    User user =
        userRepository.findById(userId).orElseThrow(() -> new ProfessorNotFoundException(userId));

    lab.removeMember(user);
    labRepository.save(lab);
  }
}
