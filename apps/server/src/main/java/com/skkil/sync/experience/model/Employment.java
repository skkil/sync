package com.skkil.sync.experience.model;

import com.skkil.sync.experience.constant.ExperienceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "employment_experiences")
public class Employment extends Experience {

  public Employment() {
    super(ExperienceType.EMPLOYMENT);
  }
}
