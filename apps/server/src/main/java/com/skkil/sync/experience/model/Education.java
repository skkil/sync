package com.skkil.sync.experience.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;

@Entity
@Table(name = "education_experiences")
@Getter
public class Education extends Experience {

  @Column(name = "major", nullable = false, length = 255)
  private String major;

  @Column(name = "gpa", precision = 3, scale = 2)
  private BigDecimal gpa;
}
