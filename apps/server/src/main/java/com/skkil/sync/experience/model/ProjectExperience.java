package com.skkil.sync.experience.model;

import com.skkil.sync.experience.constant.ExperienceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "project_experiences")
@Getter
public class ProjectExperience extends Experience {

  public ProjectExperience() {
    super(ExperienceType.PROJECT_EXPERIENCE);
  }
}
