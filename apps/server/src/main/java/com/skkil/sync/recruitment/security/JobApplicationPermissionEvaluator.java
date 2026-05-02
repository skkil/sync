package com.skkil.sync.recruitment.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.recruitment.exception.JobApplicationNotFoundException;
import com.skkil.sync.recruitment.model.JobApplication;
import com.skkil.sync.recruitment.repository.JobApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobApplicationPermissionEvaluator implements CustomPermissionEvaluator {

  private final JobApplicationRepository jobApplicationRepository;

  public JobApplicationPermissionEvaluator(JobApplicationRepository jobApplicationRepository) {
    this.jobApplicationRepository = jobApplicationRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.JOB_APPLICATION;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    if (user == null) {
      log.debug("Only authenticated users can have permissions to access job applications");
      return false;
    }

    JobApplication application = jobApplicationRepository.findById(targetId).orElse(null);
    if (application == null) {
      log.debug("Job application with id {} not found", targetId);
      throw new JobApplicationNotFoundException(targetId);
    }

    return application.getApplicant().getId().equals(user.userId());
  }
}
