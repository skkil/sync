package com.skkil.sync.recruitment.repository;

import com.skkil.sync.provider.company.model.JobPosting;
import com.skkil.sync.recruitment.model.JobApplication;
import com.skkil.sync.user.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobApplicationRepository
    extends JpaRepository<JobApplication, Long>, CustomJobApplicationRepository {

  @Query(
      """
      SELECT ja FROM JobApplication ja
      JOIN FETCH ja.jobPosting jp
      JOIN FETCH jp.company c
      WHERE ja.applicant = :applicant
      """)
  public Page<JobApplication> findByApplicant(User applicant, Pageable pageable);

  public Optional<JobApplication> findByApplicantAndJobPosting(
      User applicant, JobPosting jobPosting);

  @Query(
      """
      SELECT a FROM JobApplication a
      LEFT JOIN FETCH a.files
      WHERE a.id = :id
      """)
  public Optional<JobApplication> findWithFilesById(Long id);
}
