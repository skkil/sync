package com.skkil.sync.provider.model.company;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "job_postings")
@Getter
public class JobPosting extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Column(name = "job_title")
  private String jobTitle;

  @Column(name = "job_description", columnDefinition = "TEXT")
  private String jobDescription;

  @Column(name = "location")
  private String location;

  protected JobPosting() {}

  @Builder
  public JobPosting(Company company, String jobTitle, String jobDescription, String location) {
    this.company = company;
    this.jobTitle = jobTitle;
    this.jobDescription = jobDescription;
    this.location = location;
  }

  public void updateFields(
      String jobTitle, String jobDescription, String qualifications, String location) {
    if (jobTitle != null) {
      this.jobTitle = jobTitle;
    }

    if (jobDescription != null) {
      this.jobDescription = jobDescription;
    }

    if (location != null) {
      this.location = location;
    }
  }
}
