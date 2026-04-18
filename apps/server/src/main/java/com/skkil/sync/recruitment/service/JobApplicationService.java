package com.skkil.sync.recruitment.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.company.model.JobPosting;
import com.skkil.sync.provider.company.service.domain.JobPostingDomainService;
import com.skkil.sync.recruitment.dto.data.JobApplicationDto;
import com.skkil.sync.recruitment.dto.request.CreateJobApplicationRequest;
import com.skkil.sync.recruitment.dto.response.CreateJobApplicationResponse;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationResponse;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationsResponse;
import com.skkil.sync.recruitment.mapper.JobApplicationMapper;
import com.skkil.sync.recruitment.model.JobApplication;
import com.skkil.sync.recruitment.repository.JobApplicationRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobApplicationService {

  private final UserDomainService userDomainService;
  private final JobPostingDomainService jobPostingDomainService;
  private final JobApplicationRepository jobApplicationRepository;
  private final JobApplicationMapper jobApplicationMapper;
  private final PaginationService paginationService;

  public JobApplicationService(
      UserDomainService userDomainService,
      JobPostingDomainService jobPostingDomainService,
      JobApplicationRepository jobApplicationRepository,
      JobApplicationMapper jobApplicationMapper,
      PaginationService paginationService) {
    this.userDomainService = userDomainService;
    this.jobPostingDomainService = jobPostingDomainService;
    this.jobApplicationRepository = jobApplicationRepository;
    this.jobApplicationMapper = jobApplicationMapper;
    this.paginationService = paginationService;
  }

  @Transactional
  public CreateJobApplicationResponse createJobApplication(
      Long userId, CreateJobApplicationRequest request) {
    User applicant = userDomainService.getUserReference(userId);
    JobPosting jobPosting =
        jobPostingDomainService.findByIdAndCompanyId(
            Long.valueOf(request.jobPostingId()), Long.valueOf(request.companyId()));

    Optional<JobApplication> existingApplication =
        jobApplicationRepository.findByApplicantAndJobPosting(applicant, jobPosting);

    if (existingApplication.isPresent()) {
      return new CreateJobApplicationResponse(existingApplication.get().getId());
    }

    JobApplication application =
        JobApplication.builder().applicant(applicant).jobPosting(jobPosting).build();

    application = jobApplicationRepository.save(application);
    return new CreateJobApplicationResponse(application.getId());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("#userId == principal.userId")
  public GetJobApplicationsResponse getJobApplications(
      Long userId, OffsetPaginationRequest pagination) {
    User applicant = userDomainService.getUserReference(userId);

    var applications =
        paginationService
            .paginate(
                (pageable) -> jobApplicationRepository.findByApplicant(applicant, pageable),
                pagination)
            .map(
                ja ->
                    new GetJobApplicationsResponse.Application(
                        ja.getId(),
                        new GetJobApplicationsResponse.Company(
                            ja.getJobPosting().getCompany().getId(),
                            ja.getJobPosting().getCompany().getName())));

    return new GetJobApplicationsResponse(applications);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#applicationId, 'JOB_APPLICATION', 'READ')")
  public GetJobApplicationResponse getJobApplication(Long applicationId) {
    JobApplicationDto application = jobApplicationRepository.findApplicationById(applicationId);
    return jobApplicationMapper.toGetJobApplicationResponse(application);
  }
}
