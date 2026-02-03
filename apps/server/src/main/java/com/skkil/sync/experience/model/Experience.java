package com.skkil.sync.experience.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Table(name = "experiences")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
public abstract class Experience extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "experience_type", length = 100, nullable = false)
  private ExperienceType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  protected User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", nullable = false)
  protected Provider provider;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  protected Experience() {}

  protected Experience(ExperienceType type) {
    this.type = type;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setProvider(Provider provider) {
    this.provider = provider;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }
}
