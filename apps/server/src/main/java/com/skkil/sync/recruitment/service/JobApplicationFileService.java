package com.skkil.sync.recruitment.service;

import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.recruitment.dto.request.UploadJobApplicationFileRequest;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationFilesResponse;
import com.skkil.sync.recruitment.exception.JobApplicationNotFoundException;
import com.skkil.sync.recruitment.model.JobApplication;
import com.skkil.sync.recruitment.model.JobApplicationFile;
import com.skkil.sync.recruitment.repository.JobApplicationRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class JobApplicationFileService {

  private JobApplicationRepository jobApplicationRepository;
  private MediaDomainService mediaService;

  public JobApplicationFileService(
      JobApplicationRepository jobApplicationRepository, MediaDomainService mediaService) {
    this.jobApplicationRepository = jobApplicationRepository;
    this.mediaService = mediaService;
  }

  @Transactional
  @PreAuthorize("hasPermission(#applicationId, 'JOB_APPLICATION', 'EDIT')")
  public void uploadJobApplicationFile(
      Long uploaderId, Long applicationId, UploadJobApplicationFileRequest request) {
    JobApplicationFile jobApplicationFile = new JobApplicationFile();

    JobApplication jobApplication =
        jobApplicationRepository
            .findWithFilesById(applicationId)
            .orElseThrow(() -> new JobApplicationNotFoundException(applicationId));

    jobApplication.addFile(jobApplicationFile);

    Media file = mediaService.getUnlinkedMedia(uploaderId, request.fileId());
    jobApplicationFile.setFile(file);
    file.markAsUploaded();

    log.debug(
        "User {} uploaded file {} for job application {}",
        uploaderId,
        request.fileId(),
        applicationId);
    jobApplicationRepository.save(jobApplication);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#applicationId, 'JOB_APPLICATION', 'READ')")
  public GetJobApplicationFilesResponse getJobApplicationFiles(Long applicationId) {
    JobApplication application =
        jobApplicationRepository
            .findWithFilesById(applicationId)
            .orElseThrow(() -> new JobApplicationNotFoundException(applicationId));

    List<GetJobApplicationFilesResponse.JobApplicationFile> files =
        application.getFiles().stream()
            .map(
                applicationFile ->
                    mediaService
                        .generatePresignedGetUrl(applicationFile.getFile())
                        .toExternalForm())
            .map(GetJobApplicationFilesResponse.JobApplicationFile::new)
            .toList();

    log.debug("Found {} files for job application {}", files.size(), applicationId);
    return new GetJobApplicationFilesResponse(files);
  }
}
