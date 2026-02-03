package com.skkil.sync.experience.model;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.provider.model.Provider;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "education_experiences")
@Getter
public class Education extends Experience {

  @Column(name = "major", nullable = false, length = 255)
  private String major;

  @Column(name = "gpa", precision = 3, scale = 2)
  private BigDecimal gpa;

  protected Education() {}

  @Builder
  public Education(
      User user,
      Provider provider,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String major,
      BigDecimal gpa) {
    super(ExperienceType.EDUCATION);

    this.major = major;
    this.gpa = gpa;
  }
}
