package com.skkil.sync.recruitment.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.provider.company.model.JobPosting;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "job_applications",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"job_posting_id", "applicant_id"})})
@Getter
public class JobApplication extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_posting_id", nullable = false)
  private JobPosting jobPosting;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "applicant_id", nullable = false)
  private User applicant;

  protected JobApplication() {}

  @Builder
  public JobApplication(User applicant, JobPosting jobPosting) {
    this.applicant = applicant;
    this.jobPosting = jobPosting;
  }
}
