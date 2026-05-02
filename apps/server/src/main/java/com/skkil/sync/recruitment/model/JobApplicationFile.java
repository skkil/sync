package com.skkil.sync.recruitment.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.media.model.Media;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "job_application_files")
@Getter
public class JobApplicationFile extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_application_id", nullable = false)
  private JobApplication jobApplication;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "media_id", nullable = false)
  private Media file;

  public void setJobApplication(JobApplication jobApplication) {
    this.jobApplication = jobApplication;
  }

  public void setFile(Media file) {
    this.file = file;
  }
}
